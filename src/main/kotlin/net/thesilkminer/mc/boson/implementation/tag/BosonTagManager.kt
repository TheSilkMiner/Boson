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

object BosonTagManager : TagRegistry, TagTypeRegistry {
    private val l = L(MOD_NAME, "Tag Manager")

    private val tagMap = mutableMapOf<TagType<*>, MutableList<Tag<*>>>()

    override fun <T : Any> registerTagType(tagType: TagType<T>) {
        val name = tagType.directoryName
        val already = this.tagMap.asSequence().find { it.key.directoryName == name }
        if (already != null) {
            this.l.bigError("""
                The given tag '$name' has already been registered! Duplicate tag registration is a serious error!
                You shouldn't attempt to register tags twice: this attempt will now be blocked, but the game won't
                crash. Note that this doesn't mean this is good!
                
                REMOVE THE DUPLICATE CALL NOW!
            """.trimIndent())
            return
        }
        this.l.info("Registered tag type $tagType targeting ${tagType.directoryName}")
        this.tagMap[tagType] = mutableListOf()
    }

    override fun <T : Any> findTag(type: TagType<T>, name: NameSpacedString): Tag<T> {
        val targetList = this.tagMap[type] ?: throw IllegalStateException("Tag Type $type is not known to the Tag registry")
        val probableTag = targetList.firstOrNull { it.name == name }
        if (probableTag != null) return probableTag.uncheckedCast()
        this.l.debug("Tag $name for type $type does not exist: a new one will be created")
        val new = bosonApi.createTag(type, name)
        targetList += new
        return new
    }

    override fun <T : Any> findFor(target: T, type: TagType<T>) =
            (this.tagMap[type] ?: throw IllegalStateException("Tag Type $type is not known to the Tag registry"))
                    .asSequence()
                    .map { it.uncheckedCast<Tag<T>>() }
                    .filter { target in it }
                    .toList()

    fun <T : Any> findTagType(directoryName: String) = this.tagMap.keys.firstOrNull { it.directoryName == directoryName }?.uncheckedCast<TagType<T>>()

    fun fireTagTypeRegistrationEvent() {
        this.l.info("Attempting to gather all tag types into registry")
        this.l.debug("Clearing tag map: we don't want anybody doing interesting things like injecting directly into the registry")
        this.tagMap.clear()
        MinecraftForge.EVENT_BUS.post(TagTypeRegisterEvent(this))
        this.l.info("Tag types gathered")
        this.l.debug("Dumping found tags:")
        this.tagMap.forEach { (k, v) -> this.l.debug("  $k -> $v")}
    }
}
