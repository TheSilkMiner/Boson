package net.thesilkminer.mc.boson.api.registry

import net.minecraftforge.fml.common.eventhandler.EventBus
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
interface DeferredRegister<T : IForgeRegistryEntry<T>> {
    companion object {
        fun <T : IForgeRegistryEntry<T>> obtain(registry: IForgeRegistry<T>, owner: String): DeferredRegister<T> = TODO()

        operator fun <T : IForgeRegistryEntry<T>> invoke(registry: IForgeRegistry<T>, owner: String) = obtain(registry, owner)
    }

    val registry: IForgeRegistry<T>
    val owner: String

    fun <U : T> register(name: String, objectSupplier: () -> U): RegistryObject<U>

    fun subscribeOnto(bus: EventBus) // TODO("API EventBus out")
}
