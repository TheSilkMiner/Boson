package net.thesilkminer.mc.boson.implementation.compatibility

import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityLoader
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityProvider
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityProviderRegistry
import net.thesilkminer.mc.boson.api.log.L
import kotlin.reflect.KClass

object CompatibilityProviderManager : CompatibilityProviderRegistry {
    private val l = L(MOD_NAME, "Compatibility Provider Registry")

    private val providers = mutableSetOf<KClass<out CompatibilityProvider>>()

    override fun <T : CompatibilityProvider> registerProvider(provider: KClass<out T>) {
        if (provider in this.providers) {
            l.bigWarn("A handler for the given class ${provider.qualifiedName} was registered before: registering another is NOT supported! Addition will be skipped")
            return
        }
        this.providers += provider
    }

    override fun <T : CompatibilityProvider> findLoaderFor(provider: KClass<out T>): CompatibilityLoader<T>? = CompatibilityLoader(provider)
}
