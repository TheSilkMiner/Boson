package net.thesilkminer.mc.boson.api.registry

import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.thesilkminer.mc.boson.api.experimentalBosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
interface RegistryObject<T : IForgeRegistryEntry<in T>> {
    companion object {
        // TODO("IForgeRegistry -> Registry")
        fun <T : IForgeRegistryEntry<T>, U : T> create(name: NameSpacedString, registry: IForgeRegistry<T>): RegistryObject<U> = experimentalBosonApi.createRegistryObject(name, registry)

        operator fun <T : IForgeRegistryEntry<T>, U : T> invoke(name: NameSpacedString, registry: IForgeRegistry<T>): RegistryObject<U> = create(name, registry)
    }

    val name: NameSpacedString
    val value: T?

    fun get() = this.value!!

    operator fun invoke() = this.get()
}
