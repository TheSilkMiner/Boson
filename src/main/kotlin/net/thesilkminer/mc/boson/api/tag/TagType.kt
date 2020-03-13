package net.thesilkminer.mc.boson.api.tag

import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import kotlin.reflect.KClass

interface TagType<T : Any> {
    companion object {
        operator fun <T : Any> invoke(type: KClass<out T>, directoryName: String, toElement: (NameSpacedString) -> T, equalityEvaluator: (T, T) -> Boolean) : TagType<T> =
                bosonApi.createTagType(type, directoryName, toElement, equalityEvaluator)
        operator fun <T : Any> invoke(type: KClass<out T>, directoryName: String, toElement: (NameSpacedString) -> T) : TagType<T> =
                this(type, directoryName, toElement) { a: T, b : T -> a == b }

        fun <T : Any> find(name: String) = bosonApi.findTagType<T>(name)
    }

    val type: KClass<out T>
    val directoryName: String
    val toElement: (NameSpacedString) -> T
    val equalityEvaluator: (T, T) -> Boolean
    val name: String get() = this.directoryName
}
