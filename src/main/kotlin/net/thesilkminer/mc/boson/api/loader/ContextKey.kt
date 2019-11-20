package net.thesilkminer.mc.boson.api.loader

import net.thesilkminer.mc.boson.api.bosonApi
import kotlin.reflect.KClass

interface ContextKey<T : Any> {
    companion object {
        operator fun <T : Any> invoke(name: String, type: KClass<T>): ContextKey<T> = bosonApi.createLoaderContextKey(name, type)
    }

    val name: String
    val type: KClass<T>
}
