package net.thesilkminer.mc.boson.implementation.compatibility

import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityLoader
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityProvider
import net.thesilkminer.mc.boson.api.log.L
import java.util.ServiceLoader
import kotlin.reflect.KClass

class ServiceBasedCompatibilityLoader<T : CompatibilityProvider> private constructor(private val provider: KClass<T>) : CompatibilityLoader<T> {
    companion object {
        private val l = L(MOD_NAME, "Compatibility Loader")

        private val cache = mutableMapOf<KClass<out Any>, ServiceBasedCompatibilityLoader<out CompatibilityProvider>>()

        operator fun <T : CompatibilityProvider> invoke(provider: KClass<T>) =
                cache.computeIfAbsent(provider) { ServiceBasedCompatibilityLoader(provider) }.uncheckedCast<CompatibilityLoader<T>>()
    }

    private val lazySeq by lazy {
        l.info("Beginning discovery of implementations of compatibility provider class '${this.provider.qualifiedName}'")
        l.debug("Using ${this::class.simpleName}/$this for loading: lazily populating sequence")
        val implementations = ServiceLoader.load(this.provider.java)
        // This is mainly done so that all classes are loaded and initialized immediately, so that mistakes or weird
        // classloading issues appear immediately during discovery, rather than somewhere else
        l.info("Discovery has found ${implementations.count()} loaders: returning them as sequence now")
        implementations.asSequence().map { it!! }
    }

    override fun findProviders() = this.lazySeq
}
