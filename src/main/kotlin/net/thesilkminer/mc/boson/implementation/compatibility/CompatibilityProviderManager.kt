package net.thesilkminer.mc.boson.implementation.compatibility

import net.minecraftforge.common.MinecraftForge
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityProvider
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityProviderRegistry
import net.thesilkminer.mc.boson.api.event.CompatibilityProviderRegistryEvent
import net.thesilkminer.mc.boson.api.log.L
import kotlin.reflect.KClass

object CompatibilityProviderManager : CompatibilityProviderRegistry {
    private val l = L(MOD_NAME, "Compatibility Provider Registry")

    private val providers = mutableSetOf<KClass<out CompatibilityProvider>>()
    private val loaders = mutableMapOf<KClass<out CompatibilityProvider>, ServiceBasedCompatibilityLoader<out CompatibilityProvider>>()

    override fun <T : CompatibilityProvider> registerProvider(provider: KClass<out T>) {
        if (provider in this.providers) {
            l.bigWarn("A handler for the given class ${provider.qualifiedName} was registered before: registering another is NOT supported! Addition will be skipped")
            return
        }
        this.providers += provider
    }

    override fun findAllProviders(): Sequence<CompatibilityProvider> = this.providers.asSequence().map(this::findProviders).flatten()

    override fun <T : CompatibilityProvider> findProviders(provider: KClass<out T>): Sequence<T> =
        this.loaders[provider]?.providers?.uncheckedCast() ?: this.l.warn("Provider '${provider.qualifiedName}' wasn't registered!").let { sequenceOf<T>() }

    fun registerProviders() {
        this.l.info("Beginning provider registration")
        MinecraftForge.EVENT_BUS.post(CompatibilityProviderRegistryEvent(this))
        this.l.info("Registration completed: a total of ${this.providers.count()} were registered")
        this.l.info("Creating and discovering implementations for each of them")
        this.providers.forEach { this.loaders[it] = ServiceBasedCompatibilityLoader(it) }
        this.l.info("Providers registered")
    }

    fun fire(event: CompatibilityProvider.() -> Unit) {
        this.providers.asSequence()
                .map(this::findProviders)
                .flatten()
                .forEach(event)
    }
}
