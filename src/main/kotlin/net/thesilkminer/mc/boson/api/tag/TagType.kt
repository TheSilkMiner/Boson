package net.thesilkminer.mc.boson.api.tag

import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import kotlin.reflect.KClass

interface TagType<out T : Any> {
    companion object {
        operator fun <T : Any> invoke(type: KClass<out T>, directoryName: String, toElement: (NameSpacedString) -> T): TagType<T> = bosonApi.createTagType(type, directoryName, toElement)
        fun <T : Any> find(directoryName: String) = bosonApi.findTagType<T>(directoryName)
    }

    val type: KClass<out T>
    val directoryName: String
    val toElement: (NameSpacedString) -> T
}
