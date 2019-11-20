package net.thesilkminer.mc.boson.implementation.loader

import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.api.loader.ContextKey
import kotlin.reflect.KClass

class BosonContextKey<T : Any> private constructor(override val name: String, override val type: KClass<T>) : ContextKey<T> {
    companion object {
        private val keys = mutableMapOf<String, ContextKey<*>>()

        operator fun <T : Any> invoke(name: String, type: KClass<T>) = this.keys.computeIfAbsent(name) { BosonContextKey(name, type) }.uncheckedCast<ContextKey<T>>()
    }
}
