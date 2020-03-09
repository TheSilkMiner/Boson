package net.thesilkminer.mc.boson.implementation

import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.ExperimentalBosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.api.registry.DeferredRegister
import net.thesilkminer.mc.boson.api.registry.RegistryObject
import net.thesilkminer.mc.boson.implementation.registry.BosonDeferredRegister
import net.thesilkminer.mc.boson.implementation.registry.BosonRegistryObject

class ExperimentalBosonApiBinding : ExperimentalBosonApi {
    private val l = L(MOD_NAME, "Experimental Bindings")

    init {
        l.warn("A mod has requested Experimental APIs to be loaded: if you experience crashes it may due to mismatching versions")
    }

    override fun <T : IForgeRegistryEntry<T>> createDeferredRegister(registry: IForgeRegistry<T>, owner: String): DeferredRegister<T> = BosonDeferredRegister(registry, owner)
    override fun <T : IForgeRegistryEntry<T>, U : T> createRegistryObject(name: NameSpacedString, registry: IForgeRegistry<T>): RegistryObject<U> = BosonRegistryObject(name, registry)
}
