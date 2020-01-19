package net.thesilkminer.mc.boson.api.tag

import net.thesilkminer.mc.boson.api.id.NameSpacedString

interface TagRegistry {
    fun <T : Any> findAllTagsOf(type: TagType<T>): List<Tag<T>>
    fun <T : Any> findTag(type: TagType<T>, name: NameSpacedString): Tag<T>
    fun <T : Any> findFor(target: T, type: TagType<T>): List<Tag<T>>

    operator fun <T : Any> get(type: TagType<T>) = this.findAllTagsOf(type)
    operator fun <T : Any> get(type: TagType<T>, name: NameSpacedString) = this.findTag(type, name)
    operator fun <T : Any> get(target: T, type: TagType<T>) = this.findFor(target, type)
}
