package net.thesilkminer.mc.boson.prefab.loader.context

import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.ContextBuilder
import net.thesilkminer.mc.boson.api.loader.ContextKey
import net.thesilkminer.mc.boson.api.loader.LoadingPhase
import kotlin.reflect.full.cast

class BaseContext : Context {
    private val map = mutableMapOf<ContextKey<*>, Any>()

    override fun <T : Any> get(key: ContextKey<out T>): T? = key.type.cast(this.map[key])
    override fun <T : Any> set(key: ContextKey<out T>, value: T) = this.let { this.map[key] = value }
    override fun <T : Any> computeIfAbsent(key: ContextKey<out T>, supplier: (ContextKey<*>) -> T): T = key.type.cast(this.map.computeIfAbsent(key) { supplier(it) })
    override fun <T : Any, R> ifPresent(key: ContextKey<out T>, consumer: (T) -> R): R? = this.map[key]?.let { consumer(key.type.cast(it)) }
}

class BaseContextBuilder : ContextBuilder {
    override fun buildContext(phase: LoadingPhase<*>?, vararg any: Any): Context = BaseContext()
}
