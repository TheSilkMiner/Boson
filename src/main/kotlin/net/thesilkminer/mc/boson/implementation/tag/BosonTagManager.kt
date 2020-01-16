package net.thesilkminer.mc.boson.implementation.tag

import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.api.tag.TagRegistry
import net.thesilkminer.mc.boson.api.tag.TagType

object BosonTagManager : TagRegistry {
    private val l = L(MOD_NAME, "Tag Manager")

    private val tagMap = mutableMapOf<TagType<*>, MutableList<Tag<*>>>()

    override fun <T : Any> findTag(type: TagType<T>, name: NameSpacedString): Tag<T> {
        val targetList = this.tagMap.computeIfAbsent(type) { mutableListOf() }
        val probableTag = targetList.firstOrNull { it.name == name }
        if (probableTag != null) return probableTag.uncheckedCast()
        l.debug("Tag $name for type $type does not exist: a new one will be created")
        val new = bosonApi.createTag(type, name)
        targetList += new
        return new
    }

    override fun <T : Any> findFor(target: T, type: TagType<T>) =
            this.tagMap.computeIfAbsent(type) { mutableListOf() }
                    .asSequence()
                    .map { it.uncheckedCast<Tag<T>>() }
                    .filter { target in it }
                    .toList()
}
