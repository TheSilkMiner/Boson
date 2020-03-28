/*
 * Copyright (C) 2020  TheSilkMiner
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.thesilkminer.kotlin.commons.lang.reloadableLazy
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.event.ObjectHoldersAppliedEvent
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.api.registry.RegistryObject

internal class BosonRegistryObject<T : IForgeRegistryEntry<in T>> private constructor(override val name: NameSpacedString,
                                                                                      private val targetRegistryName: NameSpacedString?,
                                                                                      private val objectGetter: (NameSpacedString) -> T?) : RegistryObject<T> {
    companion object {
        private val allRegistryObjects = mutableListOf<BosonRegistryObject<*>>()
        private val l = L(MOD_NAME, "Registry Object Reloading")

        init { MinecraftForge.EVENT_BUS.register(this) }

        internal fun <T : IForgeRegistryEntry<T>, U : T> build(name: NameSpacedString, registry: IForgeRegistry<T>): RegistryObject<U> {
            @Suppress("RemoveExplicitTypeArguments") // Somehow type inference breaks again here
            return BosonRegistryObject<U>(name, registry.name?.let { NameSpacedString(it.namespace, it.path) }) { registry.findTarget(it) }
        }

        internal operator fun <T : IForgeRegistryEntry<T>, U : T> invoke(name: NameSpacedString, registry: IForgeRegistry<T>): RegistryObject<U> = build(name, registry)

        @SubscribeEvent
        fun onObjectHoldersApplication(event: ObjectHoldersAppliedEvent) = this.allRegistryObjects.forEach(BosonRegistryObject<*>::reload)

        private fun <T : IForgeRegistryEntry<T>, U : T> IForgeRegistry<T>.findTarget(name: NameSpacedString): U? {
            // It could be cleaner yeah, but it works
            val entryName = ResourceLocation(name.nameSpace, name.path)
            return if (this.containsKey(entryName)) this.getValue(entryName)?.uncheckedCast<U>() else null
        }
    }

    init { allRegistryObjects += this }

    private val reloadableLazy = reloadableLazy { this.objectGetter(this.name) }
    override val value: T? by reloadableLazy

    private fun reload() {
        l.debug("Reloading entry '${this.name}' from registry '${this.targetRegistryName ?: "ERROR TYPE"}'")
        this.reloadableLazy.reload()
    }

    internal fun hotReload() { this.reloadableLazy.reload() }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if ((other as? BosonRegistryObject<*>) == null) return false
        return this.name == other.name && this.targetRegistryName == other.targetRegistryName
    }

    override fun hashCode() = 31 * this.name.hashCode() + (this.targetRegistryName?.hashCode() ?: 0)

    override fun toString() = "RegistryObject{name='${this.name}',targetRegistryName='${this.targetRegistryName}'}"
}
