package net.thesilkminer.mc.boson.api.compatibility

import net.thesilkminer.mc.boson.api.bosonApi
import kotlin.reflect.KClass

interface CompatibilityLoader<out T : CompatibilityProvider> {
    companion object {
        fun <T : CompatibilityProvider> createLoaderFor(provider: KClass<T>): CompatibilityLoader<T> = bosonApi.createLoaderFor(provider)
        operator fun <T : CompatibilityProvider> invoke(provider: KClass<T>) = createLoaderFor(provider)
    }

    fun findProviders(): Sequence<T>
}
