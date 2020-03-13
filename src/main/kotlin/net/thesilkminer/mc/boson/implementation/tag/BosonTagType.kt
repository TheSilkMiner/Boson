package net.thesilkminer.mc.boson.implementation.tag

import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.tag.TagType
import kotlin.reflect.KClass

internal data class BosonTagType<T : Any>(override val type: KClass<out T>, override val directoryName: String, override val toElement: (NameSpacedString) -> T,
                                          override val equalityEvaluator: (T, T) -> Boolean) : TagType<T>
