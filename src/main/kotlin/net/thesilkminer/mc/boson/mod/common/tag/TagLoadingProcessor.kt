package net.thesilkminer.mc.boson.mod.common.tag

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.JsonUtils
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.Processor
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.api.tag.TagType
import kotlin.reflect.full.isSuperclassOf

class TagLoadingProcessor(isFirstPass: Boolean) : Processor<JsonObject> {
    /*
     * {
     *   "replace": false,
     *   "values": [
     *     "minecraft:item",
     *     "#minecraft:tag",
     *     "@minecraft:item_with_wildcard", // TODO("Support this")
     *     "@minecraft:item_with_metadata:1", // TODO("Support this")
     *     "$minecraft:block[with=state]" // TODO("Support this")
     *   ]
     * }
     */

    companion object {
        private val l = L(MOD_NAME, "Tag Loading Processor")
    }

    private val targetFun = when (isFirstPass) {
        true -> this::processVanilla
        false -> this::processGeneric
    }

    override fun process(content: JsonObject, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?) {
        this.targetFun(content, identifier)
    }

    private fun processVanilla(content: JsonObject, identifier: NameSpacedString) = this.doFileProcessing(content, identifier) {
        NameSpacedString(if (it.nameSpace == "forge") "forge" else null, it.path)
    }

    private fun processGeneric(content: JsonObject, identifier: NameSpacedString) = this.doFileProcessing(content, identifier) { it }

    private fun doFileProcessing(content: JsonObject, identifier: NameSpacedString, namingFun: (NameSpacedString) -> NameSpacedString) {
        val tagTypePath = identifier.path.substring(startIndex = 0, endIndex = identifier.path.indexOf('/'))
        val actualIdentifier = NameSpacedString(identifier.nameSpace, identifier.path.removePrefix(tagTypePath).removePrefix("/"))
        val tagType = TagType.find<Any>(tagTypePath)
        if (tagType == null) {
            l.debug("Found tag '$actualIdentifier' for unregistered tag type '$tagTypePath': skipping processing, since it may be mod compatibility")
            return
        }
        this.processTagJson(tagType, content, actualIdentifier, namingFun)
    }

    private fun <T : Any> processTagJson(tagType: TagType<T>, content: JsonObject, identifier: NameSpacedString, namingFun: (NameSpacedString) -> NameSpacedString) {
        val targetTag = bosonApi.tagRegistry.findTag(tagType, namingFun(identifier))
        // The target tag above is either just generated, or an already existing one: we just don't care
        if (JsonUtils.getBoolean(content, "replace", false)) {
            // Replacing means clear and then add, so... we can clear and treat it as just add
            -targetTag
        }
        try {
            JsonUtils.getJsonArray(content, "values").forEach { it.processTagEntry(tagType, targetTag) }
        } catch (e: JsonParseException) {
            throw JsonParseException("Unable to parse tag '${namingFun(identifier)}' for tag type '$tagType' (full name: '$identifier'): ${e.message}", e)
        }
    }

    private fun <T : Any> JsonElement.processTagEntry(tagType: TagType<T>, targetTag: Tag<T>) {
        if (!this.isJsonPrimitive || !this.asJsonPrimitive.isString) throw JsonSyntaxException("Values inside 'values' can only be strings")
        val string = this.asJsonPrimitive.asString
        try {
            string.processTagEntry(tagType, targetTag)
        } catch (e: IllegalArgumentException) {
            if (e.cause !is KotlinNullPointerException) throw e
            l.error("Unable to add '$string' to tag '${targetTag.name}', are you sure the entry exists? Error: ${e.message}")
        }
    }

    private fun <T : Any> String.processTagEntry(tagType: TagType<T>, targetTag: Tag<T>) = when (this[0]) {
        '$' -> this.substring(startIndex = 1).processStateEntry(tagType, targetTag)
        '@' -> this.substring(startIndex = 1).processMetadataEntry(tagType, targetTag)
        '#' -> this.substring(startIndex = 1).processOtherTagEntry(tagType, targetTag)
        else -> this.processItemEntry(tagType, targetTag)
    }

    private fun <T : Any> String.processItemEntry(tagType: TagType<T>, targetTag: Tag<T>) {
        targetTag += tagType.toElement(this.toNameSpacedString())
    }

    private fun <T : Any> String.processOtherTagEntry(tagType: TagType<T>, targetTag: Tag<T>) {
        val tagName = this.toNameSpacedString()
        val otherTag = bosonApi.tagRegistry.findTag(tagType, tagName)
        targetTag += otherTag
    }

    private fun <T : Any> String.processMetadataEntry(tagType: TagType<T>, targetTag: Tag<T>) {
        if (!tagType.isValidTypeForMetadata()) {
            throw JsonParseException("Unable to parse a metadata-enabled tag inside a tag of type '${tagType.name}': only 'items' is supported")
        }
        this.processStackMetadataEntry(tagType.uncheckedCast(), targetTag.uncheckedCast())
    }

    private fun String.processStackMetadataEntry(tagType: TagType<ItemStack>, targetTag: Tag<ItemStack>) {
        val lastColon = this.lastIndexOf(':')
        val probablyMeta = this.substring(startIndex = lastColon + 1)
        val meta = probablyMeta.toIntOrNull() ?: return this.processWildcardEntry(tagType, targetTag)
        this.processMetadataEntry(tagType, targetTag, meta)
    }

    private fun String.processWildcardEntry(@Suppress("UNUSED_PARAMETER") tagType: TagType<ItemStack>, targetTag: Tag<ItemStack>) {
        l.warn("Found wildcard entry '$this' inside tag '${targetTag.name}': this is not currently supported! Addition will be skipped")
        // TODO()
    }

    private fun String.processMetadataEntry(@Suppress("UNUSED_PARAMETER") tagType: TagType<ItemStack>, targetTag: Tag<ItemStack>, metadata: Int) {
        l.warn("Found metadata entry '$this' (meta: $metadata) inside tag '${targetTag.name}': this is not currently supported! Addition will be skipped")
        // TODO()
    }

    private fun <T : Any> String.processStateEntry(tagType: TagType<T>, targetTag: Tag<T>) {
        if (!tagType.isValidTypeForState()) {
            throw JsonParseException("Unable to parse a state-enabled tag inside a tag of type '${tagType.name}': only 'blocks' is supported")
        }
        this.processBlockStateEntry(tagType.uncheckedCast(), targetTag.uncheckedCast())
    }

    private fun String.processBlockStateEntry(@Suppress("UNUSED_PARAMETER") tagType: TagType<IBlockState>, targetTag: Tag<IBlockState>) {
        l.warn("Found sate entry '$this' inside tag '${targetTag.name}': this is not currently supported! Addition will be skipped")
        // TODO()
    }

    private fun <T : Any> TagType<T>.isValidTypeForMetadata() = with (this.type) { ItemStack::class.isSuperclassOf(this) }
    private fun <T : Any> TagType<T>.isValidTypeForState() = with (this.type) { IBlockState::class.isSuperclassOf(this) }

    private fun String.toNameSpacedString(): NameSpacedString = with (this.split(':', limit = 2)) {
        if (this.count() == 1) NameSpacedString(this[0]) else NameSpacedString(this[0], this[1])
    }
}
