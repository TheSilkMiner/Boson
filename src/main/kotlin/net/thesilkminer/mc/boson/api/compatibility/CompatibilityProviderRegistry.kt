package net.thesilkminer.mc.boson.api.compatibility

import kotlin.reflect.KClass

interface CompatibilityProviderRegistry {
    fun <T : CompatibilityProvider> registerProvider(provider: KClass<out T>)

    fun findAllProviders(): Sequence<CompatibilityProvider>
    fun <T : CompatibilityProvider> findProviders(provider: KClass<out T>): Sequence<T>

    operator fun <T : CompatibilityProvider> get(provider: KClass<out T>) = this.findProviders(provider)
}
