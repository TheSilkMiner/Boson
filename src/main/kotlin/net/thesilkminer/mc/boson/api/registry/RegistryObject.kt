package net.thesilkminer.mc.boson.api.registry

import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import org.jetbrains.annotations.ApiStatus
import kotlin.reflect.KClass

@ApiStatus.Experimental
interface RegistryObject<T : IForgeRegistryEntry<in T>> {
    companion object {
        // TODO("IForgeRegistry -> Registry")
        fun <T : IForgeRegistryEntry<T>, U : T> create(name: NameSpacedString, registry: IForgeRegistry<T>, targetClass: KClass<U>): RegistryObject<U> = TODO()

        operator fun <T : IForgeRegistryEntry<T>, U : T> invoke(name: NameSpacedString, registry: IForgeRegistry<T>, targetClass: KClass<U>) =
                create(name, registry, targetClass)
    }

    val name: NameSpacedString
    val value: T?

    fun get() = this.value!!

    operator fun invoke() = this.get()
}
