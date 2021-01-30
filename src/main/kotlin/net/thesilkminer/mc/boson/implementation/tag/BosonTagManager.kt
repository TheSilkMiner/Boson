/*
 * Copyright (C) 2021  TheSilkMiner
 *
 * This file is part of Boson.
 *
 * Boson is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Boson is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Boson.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact information:
 * E-mail: thesilkminer <at> outlook <dot> com
 */

package net.thesilkminer.mc.boson.implementation.tag

import net.minecraftforge.common.MinecraftForge
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.event.TagTypeRegisterEvent
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.api.tag.TagRegistry
import net.thesilkminer.mc.boson.api.tag.TagType
import net.thesilkminer.mc.boson.api.tag.TagTypeRegistry

internal object BosonTagManager : TagRegistry, TagTypeRegistry {
    private val l = L(MOD_NAME, "Tag Manager")

    private val tagMap = mutableMapOf<TagType<*>, MutableList<Tag<*>>>()

    private var tagsFrozen = false

    override fun <T : Any> registerTagType(tagType: TagType<T>) {
        val targetDirectory = tagType.directoryName
        val already = this.tagMap.asSequence().find { it.key.directoryName == targetDirectory }
        if (already != null) {
            this.l.bigError("""
                The given tag '${tagType.name}' has already been registered! Duplicate tag registration is a SERIOUS error!
                You shouldn't attempt to register tags twice: this attempt will now be blocked, but the game won't
                crash. Note that this doesn't mean this is good!
                
                REMOVE THE DUPLICATE CALL NOW!
            """.trimIndent())
            return
        }
        this.l.info("Registered tag type '${tagType.name}' -> $tagType targeting 'data/**/tags/${tagType.directoryName}/**'")
        this.tagMap[tagType] = mutableListOf()
    }

    override fun <T : Any> findAllTagsOf(type: TagType<T>) = this.listForType(type)
            .asSequence()
            .map { it.uncheckedCast<Tag<T>>() }
            .toList()

    override fun <T : Any> findTag(type: TagType<T>, name: NameSpacedString): Tag<T> {
        val targetList = this.listForType(type)
        val probableTag = targetList.firstOrNull { it.name == name }
        if (probableTag != null) return probableTag.uncheckedCast()
        this.l.debug("Tag '$name' for type '${type.name}' does not exist: a new one will be created")
        return bosonApi.createTag(type, name).apply { targetList += this }
    }

    override fun <T : Any> findFor(target: T, type: TagType<T>) = this.findAllTagsOf(type).filter { target in it }

    override val isFrozen: Boolean get() = this.tagsFrozen

    internal fun <T : Any> findTagType(name: String) = this.tagMap.keys.firstOrNull { it.name == name }?.uncheckedCast<TagType<T>>()

    internal fun fireTagTypeRegistrationEvent() {
        this.l.info("Attempting to gather all tag types into registry")
        this.l.debug("Clearing tag map: we don't want anybody doing interesting things like injecting directly into the registry")
        this.tagMap.clear()
        MinecraftForge.EVENT_BUS.post(TagTypeRegisterEvent(this))
        this.l.info("Tag types gathered")
        this.l.debug("Dumping found tags:")
        this.tagMap.forEach { (k, v) -> this.l.debug("  $k -> $v")}
    }

    internal fun freezeTags() {
        this.tagsFrozen = true
        this.l.info("Tags frozen")
    }

    private fun <T : Any> listForType(type: TagType<T>) = this.tagMap[type] ?: throw IllegalStateException("Tag type '${type.name}' is not known to the Tag registry")
}
