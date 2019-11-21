package net.thesilkminer.mc.boson.api.loader

interface Context {
    operator fun <T : Any> get(key: ContextKey<out T>): T?
    operator fun <T : Any> set(key: ContextKey<out T>, value: T)
    fun <T : Any> computeIfAbsent(key: ContextKey<out T>, supplier: (ContextKey<*>) -> T): T
    fun <T : Any, R> ifPresent(key: ContextKey<out T>, consumer: (T) -> R): R?
}
