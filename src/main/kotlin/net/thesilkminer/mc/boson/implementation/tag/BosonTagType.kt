package net.thesilkminer.mc.boson.implementation.tag

import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.tag.TagType
import kotlin.reflect.KClass

data class BosonTagType<out T : Any>(override val type: KClass<out T>, override val directoryName: String, override val toElement: (NameSpacedString) -> T) : TagType<T>
