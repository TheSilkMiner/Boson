package net.thesilkminer.mc.boson.api.loader

interface Context {
    operator fun <T : Any> get(key: ContextKey<T>): T?
    operator fun <T : Any> set(key: ContextKey<T>, value: T)
    fun <T : Any> computeIfAbsent(key: ContextKey<T>, supplier: (ContextKey<*>) -> T): T
    fun <T : Any, R> ifPresent(key: ContextKey<T>, consumer: (T) -> R): R?
}
