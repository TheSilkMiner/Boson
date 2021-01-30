/*
 * Copyright (C) 2021  TheSilkMiner
 *
 * This file is part of Boson.
 *
 * Boson is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Boson is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Boson.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact information:
 * E-mail: thesilkminer <at> outlook <dot> com
 */

package net.thesilkminer.mc.boson.implementation.registry

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.EventBus
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.minecraftforge.registries.RegistryBuilder
import net.minecraftforge.registries.RegistryManager
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.api.registry.DeferredRegister
import net.thesilkminer.mc.boson.api.registry.RegistryObject
import net.thesilkminer.mc.boson.prefab.naming.toResourceLocation
import kotlin.reflect.KClass

internal class BosonDeferredRegister<T : IForgeRegistryEntry<T>>(override val owner: String, override val registryType: KClass<T>, private var registryStorage: IForgeRegistry<T>?,
                                                                 private val registryFactory: (() -> RegistryBuilder<T>)?) : DeferredRegister<T> {
    private companion object {
        private val l = L(MOD_NAME, "DeferredRegister")

        private fun <T : IForgeRegistryEntry<T>> prepareRegistry(owner: String, name: String, type: KClass<T>, factory: RegistryBuilder<T>.() -> Unit): RegistryBuilder<T> =
                RegistryBuilder<T>().setName(NameSpacedString(owner, name).toResourceLocation()).setType(type.java).apply { this.factory() }
    }

    constructor(owner: String, registryType: KClass<T>) : this(owner, registryType, null, null)
    constructor(owner: String, registry: IForgeRegistry<T>) : this(owner, registry.registrySuperType.kotlin, registry, null)
    constructor(owner: String, registryType: KClass<T>, name: String, registryFactory: RegistryBuilder<T>.() -> Unit)
            : this(owner, registryType, null, { prepareRegistry(owner, name, registryType, registryFactory) })

    private val entries = mutableListOf<Pair<RegistryObject<*>, () -> T>>()
    private var hasRegistered = false

    override val registry: IForgeRegistry<T> get() = this.registryStorage ?: throw IllegalStateException("Unable to obtain custom registry before it has been registered")

    override fun <U : T> register(name: String, objectSupplier: () -> U): RegistryObject<U> {
        if (this.hasRegistered) throw IllegalStateException("Cannot register entries to DeferredRegister after RegistryEvent.Register has been fired")

        val entryName = NameSpacedString(this.owner, name.ensureValid())
        val registryObject = if (this.registryStorage != null) RegistryObject<T, U>(entryName, this.registry) else RegistryObject<T, U>(entryName, this.registryType, this.owner)

        if (this.entries.any { it.first == registryObject }) throw IllegalStateException("Found duplicate registration for name '$entryName'")

        this.entries += Pair(registryObject) { objectSupplier().setRegistryName(ResourceLocation(entryName.nameSpace, entryName.path)) }

        return registryObject
    }

    override fun subscribeOnto(bus: EventBus) = MinecraftForge.EVENT_BUS.register(this)

    @SubscribeEvent
    fun create(@Suppress("UNUSED_PARAMETER") event: RegistryEvent.NewRegistry) {
        if (this.registryStorage == null) {
            this.registryFactory.let {
                if (it == null) {
                    l.info("Attempted to create a DeferredRegister for an unknown registry (base class: ${this.registryType.qualifiedName}) without a factory: assuming it's a look-up")
                    return
                }
                this.registryStorage = it().create()
                l.info("Successfully created registry '${this.registryStorage?.name ?: "ERROR!"}' for base class '${this.registryType.qualifiedName}'")
            }
        }
    }

    @SubscribeEvent
    fun register(event: RegistryEvent.Register<*>) {
        if (this.registryStorage == null) {
            l.info("Reached registration phase, but no registry was assigned to this DeferredRegister: attempting to look up a registry for '${this.registryType.qualifiedName}'")
            RegistryManager.ACTIVE.getRegistry(this.registryType.java).let {
                if (it == null) throw IllegalStateException("Unable to lookup registry of type '${this.registryType.qualifiedName}' for owner '${this.owner}'")
                this.registryStorage = it
                l.info("Successfully looked up registry '${this.registryStorage?.name ?: "ERROR!"}'")
            }
        }

        if (!this.shouldLoad(event.forgeRegistry, this.registry)) return

        val registry = event.forgeRegistry.uncheckedCast<IForgeRegistry<T>>()
        l.info("Performing deferred registration for registry '${registry.name ?: registry.registrySuperType.kotlin.qualifiedName ?: "ERROR TYPE"}' for owner '${this.owner}'")

        this.entries.forEach { registry.register(it.second()).also { _ -> it.first.attemptHotReload() } }

        this.hasRegistered = true
    }

    private fun shouldLoad(eventRegistry: IForgeRegistry<*>, otherRegistry: IForgeRegistry<*>): Boolean {
        val eventName = eventRegistry.name ?: return this.fallbackShouldLoad(eventRegistry, otherRegistry)
        val otherName = otherRegistry.name ?: return this.fallbackShouldLoad(eventRegistry, otherRegistry)
        return NameSpacedString(eventName.namespace, eventName.path) == NameSpacedString(otherName.namespace, otherName.path)
    }

    private fun fallbackShouldLoad(eventRegistry: IForgeRegistry<*>, otherRegistry: IForgeRegistry<*>) =
            eventRegistry.registrySuperTypeDirect.kotlin == otherRegistry.registrySuperTypeDirect.kotlin

    private fun String.ensureValid() =
            if (this.contains(':')) throw IllegalStateException("Illegal colon character in name '$this': use a different DeferredRegistry for different owners") else this

    private fun RegistryObject<*>.attemptHotReload() = if (this is BosonRegistryObject) this.hotReload() else Unit

    @Suppress("UsePropertyAccessSyntax") // For some reason this creates a problem with the Kotlin compiler I don't know why
    private val RegistryEvent.Register<*>.forgeRegistry get() = this.getRegistry()

    // Working around Kotlin's type projections
    private val IForgeRegistry<*>.registrySuperTypeDirect get() = this::class.java.getDeclaredMethod("getRegistrySuperType").invoke(this).uncheckedCast<Class<*>>()
}
