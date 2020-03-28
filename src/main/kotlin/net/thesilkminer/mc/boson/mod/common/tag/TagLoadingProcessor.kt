/*
 * Copyright (C) 2020  TheSilkMiner
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

package net.thesilkminer.mc.boson.mod.common.tag

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.JsonUtils
import net.minecraftforge.oredict.OreDictionary
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

internal class TagLoadingProcessor(isFirstPass: Boolean) : Processor<JsonObject> {
    /*
     * {
     *   "replace": false,
     *   "values": [
     *     "minecraft:item",
     *     "#minecraft:tag",
     *     "@minecraft:item_with_wildcard",
     *     "@minecraft:item_with_metadata:1",
     *     "$minecraft:block[with=state]"
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
            throw JsonParseException("Unable to parse tag '${namingFun(identifier)}' for tag type '${tagType.name}' (full name: '$identifier'): ${e.message}", e)
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
        @Suppress("GrazieInspection")
        if (probablyMeta.isEmpty()) throw JsonSyntaxException("Invalid item metadata definition '$this' for tag '${targetTag.name}': extraneous colon at the end of the line")
        val meta = probablyMeta.toIntOrNull() ?: return this.processWildcardEntry(tagType, targetTag)
        this.substring(startIndex = 0, endIndex = lastColon).processMetadataEntry(tagType, targetTag, meta)
    }

    private fun String.processWildcardEntry(tagType: TagType<ItemStack>, targetTag: Tag<ItemStack>) {
        this.processMetadataEntry(tagType, targetTag, OreDictionary.WILDCARD_VALUE)
    }

    private fun String.processMetadataEntry(tagType: TagType<ItemStack>, targetTag: Tag<ItemStack>, metadata: Int) {
        val normalStack = tagType.toElement(this.toNameSpacedString())
        targetTag += ItemStack(normalStack.item, 1, metadata)
    }

    private fun <T : Any> String.processStateEntry(tagType: TagType<T>, targetTag: Tag<T>) {
        if (!tagType.isValidTypeForState()) {
            throw JsonParseException("Unable to parse a state-enabled tag inside a tag of type '${tagType.name}': only 'blocks' is supported")
        }
        this.processBlockStateEntry(tagType.uncheckedCast(), targetTag.uncheckedCast())
    }

    private fun String.processBlockStateEntry(tagType: TagType<IBlockState>, targetTag: Tag<IBlockState>) {
        if (this.isEmpty() || this.last() != ']') throw JsonSyntaxException("Invalid block state definition '$this' for tag '${targetTag.name}': missing ]")
        val beginning = this.indexOf('[').apply { if (this == -1) throw JsonSyntaxException("Invalid block state definition '$this' for tag '${targetTag.name}': missing block state") }
        val subProperties = this.substring(startIndex = beginning + 1).removeSuffix("]")
        val propertiesList = if (subProperties.isBlank()) listOf() else subProperties.split(',')
        val name = this.substring(startIndex = 0, endIndex = beginning)
        val defaultState = tagType.toElement(name.toNameSpacedString()).block.blockState
        val actualStates = try {
            defaultState.populateWithProperties(propertiesList)
        } catch (e: JsonParseException) {
            throw JsonParseException("Invalid block state definition '$this' for tag '${targetTag.name}': ${e.message}", e)
        }
        actualStates.forEach { targetTag += it }
    }

    private fun BlockStateContainer.populateWithProperties(properties: List<String>): List<IBlockState> {
        return this.populateWithProperties(properties.asSequence().map { it.split('=', limit = 2) }.map { it[0] to it[1] }.toMap())
    }

    private fun BlockStateContainer.populateWithProperties(properties: Map<String, String>): List<IBlockState> {
        val allProperties = this.properties.asSequence()
                .map { it.getName() as String to it }
                .toMap()
                .toMutableMap()

        var defaultedBlockState = this.baseState

        properties.forEach { (name, value) ->
            if (name !in allProperties) throw JsonParseException("No property '$name' can be found inside the block state container")
            val property = allProperties.getOrElse(name) { throw IllegalStateException("null over $name in $allProperties") }
            allProperties.remove(name)
            val propertyValue = property.parseValue(value).toJavaUtil().orElseThrow { JsonParseException("Value '$value' is not a valid value for the block state property '$name'") }
            defaultedBlockState = defaultedBlockState.applyProperty<Comparable<Any>>(property.uncheckedCast(), propertyValue)
        }

        val validBlockStates = mutableListOf<IBlockState>()
        defaultedBlockState.createCombinatorics(allProperties.toMap(), validBlockStates)
        return validBlockStates
    }

    private fun IBlockState.createCombinatorics(properties: Map<String, IProperty<*>>, list: MutableList<IBlockState>) {
        if (properties.count() == 0) {
            list += this
            return
        }
        val thisPropertyEntry = properties.asSequence().first()
        val allTheOthers = properties.toMutableMap().apply { this.remove(thisPropertyEntry.key) }.toMap()
        val allowedValues = thisPropertyEntry.value.getAllowedValues()
        allowedValues.forEach {
            val nextBlockState = this.applyProperty<Comparable<Any>>(thisPropertyEntry.value.uncheckedCast(), it)
            nextBlockState.createCombinatorics(allTheOthers, list)
        }
    }

    private fun <T : Comparable<T>> IBlockState.applyProperty(property: IProperty<T>, value: Comparable<*>) = this.withProperty(property, value.uncheckedCast())

    private fun <T : Any> TagType<T>.isValidTypeForMetadata() = with (this.type) { ItemStack::class.isSuperclassOf(this) }
    private fun <T : Any> TagType<T>.isValidTypeForState() = with (this.type) { IBlockState::class.isSuperclassOf(this) }

    private fun String.toNameSpacedString(): NameSpacedString = with (this.split(':', limit = 2)) {
        if (this.count() == 1) NameSpacedString(this[0]) else NameSpacedString(this[0], this[1])
    }
}
