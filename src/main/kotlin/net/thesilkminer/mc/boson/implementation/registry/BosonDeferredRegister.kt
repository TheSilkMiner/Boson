package net.thesilkminer.mc.boson.implementation.registry

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.EventBus
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.api.registry.DeferredRegister
import net.thesilkminer.mc.boson.api.registry.RegistryObject

internal class BosonDeferredRegister<T : IForgeRegistryEntry<T>>(override val registry: IForgeRegistry<T>, override val owner: String) : DeferredRegister<T> {
    private companion object {
        private val l = L(MOD_NAME, "DeferredRegister")
    }

    private val entries = mutableListOf<Pair<RegistryObject<*>, () -> T>>()

    override fun <U : T> register(name: String, objectSupplier: () -> U): RegistryObject<U> {
        val entryName = NameSpacedString(this.owner, name.ensureValid())
        val registryObject = RegistryObject<T, U>(entryName, this.registry)

        if (this.entries.any { it.first == registryObject }) throw IllegalStateException("Found duplicate registration for name '$entryName'")

        this.entries += Pair(registryObject) { objectSupplier().setRegistryName(ResourceLocation(entryName.nameSpace, entryName.path)) }

        return registryObject
    }

    override fun subscribeOnto(bus: EventBus) = MinecraftForge.EVENT_BUS.register(this)

    @SubscribeEvent
    fun register(event: RegistryEvent.Register<*>) {
        if (!this.shouldLoad(event.forgeRegistry, this.registry)) return

        val registry = event.forgeRegistry.uncheckedCast<IForgeRegistry<T>>()
        l.info("Performing deferred registration for registry '${registry.name ?: registry.registrySuperType.kotlin.qualifiedName ?: "ERROR TYPE"}' for owner '${this.owner}'")

        this.entries.forEach { registry.register(it.second()).also { _ -> it.first.attemptHotReload() } }
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
