package net.thesilkminer.mc.boson.api.compatibility

import kotlin.reflect.KClass

interface CompatibilityProviderRegistry {
    fun <T : CompatibilityProvider> registerProvider(provider: KClass<out T>)

    fun <T : CompatibilityProvider> findLoaderFor(provider: KClass<out T>): CompatibilityLoader<T>?
}
