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

@file:JvmName("JC")

package net.thesilkminer.mc.boson.implementation.configuration

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.stream.JsonReader
import net.thesilkminer.mc.boson.api.configuration.Category
import net.thesilkminer.mc.boson.api.configuration.Configuration
import net.thesilkminer.mc.boson.api.configuration.ConfigurationBuilder
import net.thesilkminer.mc.boson.api.configuration.ConfigurationCategoryBuilder
import net.thesilkminer.mc.boson.api.configuration.ConfigurationEntryBuilder
import net.thesilkminer.mc.boson.api.configuration.ConfigurationFormat
import net.thesilkminer.mc.boson.api.configuration.Entry
import net.thesilkminer.mc.boson.api.configuration.EntryType
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

internal class JsonConfiguration(builder: ConfigurationBuilder): Configuration {
    companion object {
        const val JSON_CONFIGURATION_FILE_EXTENSION = "json"
    }

    private val backend by lazy { JsonObject().apply { this.populateFrom(builder) } }

    override val format = ConfigurationFormat.JSON
    override val targetDistribution = builder.targetDistribution
    override val owner = builder.owner
    override val name = builder.name
    override val location = builder.constructPath(this.format)
    override val categories: List<Category> = this.backend.entrySet().map { it.value.wrapToCategory(it.key) }

    override fun save() = prettyJsonWriter.toJson(this.backend).writeTo(this.location)
    override fun load() = this.backend.accept(this.loadWithoutAccepting())

    override operator fun get(category: String, vararg subCategories: String) = this.categories.first { it.name == category }.let {
        val count = subCategories.count()
        return@let when {
            count <= 0 -> it
            count == 1 -> it.getSubCategory(subCategories[0])
            else -> {
                val subSubCategories = subCategories.toList().subList(fromIndex = 1, toIndex = count).toTypedArray()
                it.getSubCategory(subCategories[0], *subSubCategories)
            }
        }
    }

    override fun toString() = "JsonConfiguration(format=${this.format}, owner='${this.owner}', name='${this.name}', categories=${this.categories})"

    private fun loadWithoutAccepting() = prettyJsonWriter.fromJson<JsonObject>(this.location.toJsonReader(), JsonObject::class.java)

    private fun JsonObject.accept(other: JsonObject) {
        val properties = setOf(*this.entrySet().toTypedArray())
        properties.forEach { this.remove(it.key) }

        other.entrySet().forEach { this.add(it.key, it.value) }
    }
    private fun JsonObject.populateFrom(builder: ConfigurationBuilder) {
        this.accept(this@JsonConfiguration.loadWithoutAccepting())

        // Now we need to check and add any category specified in the builder if they don't exist,
        // and we need to do it recursively. Also, for every category not in the name we need to
        // delete it from the file

        // Also, to ensure that we do not replicate code, we'll have to create a dummy category
        // builder that has all the categories
        val dummyBuilder = ConfigurationCategoryBuilder("dummy_name")
        dummyBuilder.subCategories { this.addAll(builder.categories()) }
        // And now we pass this object to the accept category method, ensuring we are not adding
        // it to the actual object
        this.acceptCategory(dummyBuilder)
    }
    private fun JsonObject.acceptCategory(builder: ConfigurationCategoryBuilder) {
        // We need to check ONLY the current JsonObject, without recurring on the actual
        // name of the category. We assume that the given builder is correct and has already
        // been checked, so that we only need to check the sub categories and the entries

        // A better run-down of the method is:
        // - grab all sub categories
        // - check that they are present
        // - check that their names, comments, lang keys etc are correct
        // - recursively check for all the sub sub categories
        // - grab all entries
        // - check that they are present
        // - check that their names, comments, lang keys etc are correct
        // - remove all entries that are not present in the builder (same: no left-over policy)
        // - remove all sub categories that are not present in the builder (we don't want left-overs)

        // First off, let's grab all the categories that are present in this JSON Object. Note that
        // we assume we are in the "categories" section of the parent JSON Object. Callers should not
        // violate this assumption.
        val currentlyPresentCategories = mutableSetOf(*this.entrySet().toTypedArray()) // Make a copy, so that we don't mess up

        // Now we recurse over all sub categories
        builder.subCategories().forEach {
            val key = it.name

            // Check if they're present
            if (!this.has(key)) {
                // If they're not present, we need to create the corresponding category
                val categoryObject = JsonObject()

                // By default, we add two empty objects, one for categories and one for entries
                // This way the job outside this if expression is easier
                categoryObject.add(JSON_CAT_MARKER, JsonObject())
                categoryObject.add(JSON_DOG_MARKER, JsonObject())

                // And add it
                this.add(key, categoryObject)
            } else {
                // If it is present, we remove it from the list of categories that are present
                currentlyPresentCategories.removeIf { entry -> entry.key == key }
            }

            val foundEntry = this.get(key) as JsonObject // Guaranteed to exist, because we added it or it was already there

            // Now we check their comments and language keys
            if (it.comment.isNotEmpty()) foundEntry.add(JSON_COMMENT_MARKER, JsonPrimitive(it.comment))
            else if (foundEntry.has(JSON_COMMENT_MARKER)) foundEntry.remove(JSON_COMMENT_MARKER)

            if (it.languageKey.isNotEmpty()) foundEntry.add(JSON_LANG_KEY_MARKER, JsonPrimitive(it.comment))
            else if (foundEntry.has(JSON_LANG_KEY_MARKER)) foundEntry.remove(JSON_LANG_KEY_MARKER)

            // And now we recurse for all subcategories
            val subCategories = foundEntry[JSON_CAT_MARKER] as JsonObject
            subCategories.acceptCategory(it)

            // Now we check all the entries
            val entries = foundEntry[JSON_DOG_MARKER] as JsonObject
            entries.acceptEntries(it)

            // All the entries steps have been performed in that other method. We are free to
            // quit from this checking loop.
        }

        // And now it's time to remove all sub categories that are not present in the builder but are in
        // this JSON object
        currentlyPresentCategories.forEach { this.remove(it.key) }
    }
    private fun JsonObject.acceptEntries(builder: ConfigurationCategoryBuilder) {
        // As before, we assume that we are in the "entries" section of a JSON Object.

        // As outlined in previous documentation, the steps we need to take are the following:
        // - grab all entries
        // - check that they are present
        // - check that their names, comments, lang keys etc are correct
        // - remove all entries that are not present in the builder (same: no left-over policy)

        // First off we grab all the entries in this JSON Object.
        val currentlyStoredEntries = mutableSetOf(*this.entrySet().toTypedArray())

        // Now we recurse over all entries
        builder.entries().forEach {
            val key = it.name

            // Check if they're present
            if (!this.has(key)) {
                // If they're not present, we need to create the corresponding entry
                val entryObject = JsonObject()

                // And add it
                this.add(key, entryObject)
            } else {
                // If it is present, we remove it from the list of entries that are present
                currentlyStoredEntries.removeIf { entry -> entry.key == key }
            }

            val foundEntry = this.get(key) as JsonObject // Guaranteed to exist, because we added it or it was already there

            // Now we check their comments, language keys...
            if (it.comment.isNotEmpty()) foundEntry.add(JSON_COMMENT_MARKER, JsonPrimitive(it.comment))
            else if (foundEntry.has(JSON_COMMENT_MARKER)) foundEntry.remove(JSON_COMMENT_MARKER)

            if (it.languageKey.isNotEmpty()) foundEntry.add(JSON_LANG_KEY_MARKER, JsonPrimitive(it.comment))
            else if (foundEntry.has(JSON_LANG_KEY_MARKER)) foundEntry.remove(JSON_LANG_KEY_MARKER)

            foundEntry.add(JSON_INTERNAL_MARKER, JsonPrimitive(it.encodeInternalData()))

            // And now we check whether the value exists, if not we add its default
            if (!foundEntry.has(JSON_VALUE_MARKER)) foundEntry.add(JSON_VALUE_MARKER, it.encodeValueForDefault())

            // All the entries steps have been performed in that other method. We are free to
            // quit from this checking loop.
        }

        // And now it's time to remove all entries that are not present in the builder but are in
        // this JSON object
        currentlyStoredEntries.forEach { this.remove(it.key) }
    }

    private fun String.writeTo(path: Path) = Files.newBufferedWriter(path, StandardCharsets.UTF_8).use {
        it.write(this)
        it.write('\n'.toInt())
    }
    private fun Path.toJsonReader() = JsonReader(Files.newBufferedReader(this.createIfNotExist(), StandardCharsets.UTF_8))
    private fun Path.createIfNotExist() = if (!Files.exists(this)) this.apply { "{}".writeTo(this) } else this
}

private class JsonConfigurationCategory(private val key: String, private val backend: JsonObject) : Category {
    override val name = this.key
    override val comment = this.backend[JSON_COMMENT_MARKER]?.asString ?: ""
    override val languageKey = this.backend[JSON_LANG_KEY_MARKER]?.asString ?: ""
    override val categories = this.backend[JSON_CAT_MARKER]!!.asJsonObject.entrySet().map { it.value.wrapToCategory(it.key) }
    override val entries = this.backend[JSON_DOG_MARKER]!!.asJsonObject.entrySet().map { it.value.wrapToEntry(it.key) }

    override fun getSubCategory(category: String, vararg subCategories: String) = this.categories.first { it.name == category }.let {
        val count = subCategories.count()
        return@let when {
            count <= 0 -> it
            count == 0 -> it.getSubCategory(subCategories[0])
            else -> {
                val subSubCategories = subCategories.toList().subList(fromIndex = 1, toIndex = count).toTypedArray()
                it.getSubCategory(subCategories[0], *subSubCategories)
            }
        }
    }

    override operator fun get(entry: String) = this.entries.first { it.name == entry }

    override fun toString() = "JsonConfigurationCategory(name='${this.name}', comment='${this.comment}', " +
            "languageKey='${this.languageKey}', subcategories=${this.categories}, entries=${this.entries})"
}

private sealed class JsonConfigurationEntry(private val key: String, protected val backend: JsonObject) : Entry {
    protected val decodedData by lazy { this.backend[JSON_INTERNAL_MARKER]!!.asString.decodeInternalData() }
    @Suppress("UNCHECKED_CAST") private val props by lazy { this.decodedData[JSON_DCD_PROP] as List<Boolean>? ?: throw JsonIllegalConfigurationEntryStructure("Missing properties") }
    protected val jsonValue get() = this.backend[JSON_VALUE_MARKER]!!

    final override val name = this.key
    abstract override val type: EntryType
    final override val comment = this.backend[JSON_COMMENT_MARKER]?.asString ?: ""
    final override val languageKey = this.backend[JSON_LANG_KEY_MARKER]?.asString ?: ""
    final override val default by lazy { this.decodedData[JSON_DCD_DEF] ?: throw JsonIllegalConfigurationEntryStructure("Unable to find default data") }
    final override val requiresMcRestart by lazy { this.props[0] }
    final override val requiresWorldReload by lazy { this.props[1] }
    final override val hasSlider by lazy { this.props[2] }
    abstract override val bounds: Pair<Any?, Any?>
    abstract override var currentValue: Any

    override fun toString() = "JsonConfigurationEntry[${this::class.simpleName}](name='${this.name}', type=${this.type}, comment='${this.comment}', " +
            "languageKey='${this.languageKey}', default=${this.default}, requiresMcRestart=${this.requiresMcRestart}, requiresWorldReload=${this.requiresWorldReload}, " +
            "hasSlider=${this.hasSlider}, bounds=${this.bounds}, currentValue=${this.currentValue})"

    protected fun throwTypeMismatch(): Nothing =
            throw JsonIllegalValueForEntryException("This entry does not store a value that matches the type ${this.type}")
    protected fun throwTypeMismatch(attempt: Any): Nothing =
            throw JsonIllegalValueForEntryException("Unable to assign the value '$attempt' to a property of type ${this.type}")

    protected fun Any.encode() = this.encodeValue(this@JsonConfigurationEntry.type)
}

private class JsonBooleanConfigurationEntry(key: String, backend: JsonObject) : JsonConfigurationEntry(key, backend) {
    override val type = EntryType.BOOLEAN
    override val bounds = Pair(null, null)
    override var currentValue: Any
        get() = if (this.jsonValue.isJsonPrimitive && this.jsonValue.asJsonPrimitive.isBoolean) this.jsonValue.asBoolean else this.throwTypeMismatch()
        set(value) = if (value is Boolean) this.backend.add(JSON_VALUE_MARKER, value.encode()) else this.throwTypeMismatch(value)
}

private class JsonRealNumberConfigurationEntry(key: String, backend: JsonObject) : JsonConfigurationEntry(key, backend) {
    override val type = EntryType.REAL_NUMBER
    override val bounds: Pair<Any?, Any?> = this.decodedData[JSON_DCD_BOUNDS] as Pair<Any?, Any?>
    override var currentValue: Any
        get() = if (this.jsonValue.isJsonPrimitive && this.jsonValue.asJsonPrimitive.isNumber) this.jsonValue.asDouble.checkBounded() else this.throwTypeMismatch()
        set(value) = when (value) {
            is Double -> this.backend.add(JSON_VALUE_MARKER, value.checkBounded().encode())
            is Float -> this.backend.add(JSON_VALUE_MARKER, value.toDouble().checkBounded().encode())
            else -> this.throwTypeMismatch(value)
        }

    private fun Double.checkBounded(): Double {
        val minBound = this@JsonRealNumberConfigurationEntry.bounds.first as Double? ?: -Double.MAX_VALUE
        val maxBound = this@JsonRealNumberConfigurationEntry.bounds.second as Double? ?: Double.MAX_VALUE
        if (minBound > this || maxBound < this) throw JsonIllegalValueForEntryException("Value $this was not between bounds $minBound and $maxBound")
        return this
    }
}

private class JsonWholeNumberConfigurationEntry(key: String, backend: JsonObject) : JsonConfigurationEntry(key, backend) {
    override val type = EntryType.WHOLE_NUMBER
    override val bounds: Pair<Any?, Any?> = this.decodedData[JSON_DCD_BOUNDS] as Pair<Any?, Any?>
    override var currentValue: Any
        get() = if (this.jsonValue.isJsonPrimitive && this.jsonValue.asJsonPrimitive.isNumber) this.jsonValue.asLong.checkBounded() else this.throwTypeMismatch()
        set(value) = when (value) {
            is Long -> this.backend.add(JSON_VALUE_MARKER, value.checkBounded().encode())
            is Int -> this.backend.add(JSON_VALUE_MARKER, value.toLong().checkBounded().encode())
            is Short -> this.backend.add(JSON_VALUE_MARKER, value.toLong().checkBounded().encode())
            is Byte -> this.backend.add(JSON_VALUE_MARKER, value.toLong().checkBounded().encode())
            else -> this.throwTypeMismatch(value)
        }

    private fun Long.checkBounded(): Long {
        val minBound = this@JsonWholeNumberConfigurationEntry.bounds.first as Long? ?: Long.MIN_VALUE
        val maxBound = this@JsonWholeNumberConfigurationEntry.bounds.second as Long? ?: Long.MAX_VALUE
        if (minBound > this || maxBound < this) throw JsonIllegalValueForEntryException("Value $this was not between bounds $minBound and $maxBound")
        return this
    }
}

private class JsonStringConfigurationEntry(key: String, backend: JsonObject) : JsonConfigurationEntry(key, backend) {
    override val type = EntryType.STRING
    override val bounds = Pair(null, null)
    override var currentValue: Any
        get() = if (this.jsonValue.isJsonPrimitive && this.jsonValue.asJsonPrimitive.isString) this.jsonValue.asString!! else this.throwTypeMismatch()
        set(value) = if (value is String) this.backend.add(JSON_VALUE_MARKER, value.encode()) else this.throwTypeMismatch(value)
}

private class JsonAnyConfigurationEntry(key: String, backend: JsonObject) : JsonConfigurationEntry(key, backend) {
    override val type = EntryType.OBJECT
    override val bounds = Pair(null, null)
    override var currentValue: Any
        get() = if (this.jsonValue.isJsonObject) this.jsonValue.asJsonObject.decode(this.decodedData[JSON_DCD_DEF]) else this.throwTypeMismatch()
        set(value) = this.backend.add(JSON_VALUE_MARKER, value.ensureTypeMatch().encode())

    private fun Any.ensureTypeMatch(): Any {
        val def = this@JsonAnyConfigurationEntry.decodedData[JSON_DCD_DEF] ?: throw JsonConfigurationInvalidDefaultStructure("null default")
        if (this::class.java != def::class.java) this@JsonAnyConfigurationEntry.throwTypeMismatch(this)
        return this
    }
}

private sealed class JsonListConfigurationEntry(key: String, backend: JsonObject) : JsonConfigurationEntry(key, backend) {
    abstract override val type: EntryType
    final override val bounds = Pair(null, null)
    abstract override var currentValue: Any

    @Suppress("UNCHECKED_CAST")
    protected fun <T> checkAndCast(any: Any): List<T> = if (this.checkListType(any)) any as List<T> else this.throwTypeMismatch(any)
    protected fun checkListType(any: Any) : Boolean {
        return if (any is List<*>) {
            if (any.isEmpty()) true
            else this.isValidType(any[0])
        } else {
            false
        }
    }
    abstract fun isValidType(any: Any?): Boolean
}

private class JsonBooleanListConfigurationEntry(key: String, backend: JsonObject) : JsonListConfigurationEntry(key, backend) {
    override val type = EntryType.LIST_OF_BOOLEANS
    override var currentValue: Any
        get() = if (this.jsonValue.isJsonArray) this.jsonValue.asJsonArray.decodeIntoList { it.asBoolean } else this.throwTypeMismatch()
        set(value) = this.backend.add(JSON_VALUE_MARKER, this.checkAndCast<Boolean>(value).encode())
    override fun isValidType(any: Any?) = any is Boolean
}

private class JsonRealNumberListConfigurationEntry(key: String, backend: JsonObject) : JsonListConfigurationEntry(key, backend) {
    override val type = EntryType.LIST_OF_REAL_NUMBERS
    override var currentValue: Any
        get() = if (this.jsonValue.isJsonArray) this.jsonValue.asJsonArray.decodeIntoList { it.asDouble } else this.throwTypeMismatch()
        set(value) = this.backend.add(JSON_VALUE_MARKER, this.checkAndCast<Double>(value).encode())
    override fun isValidType(any: Any?) = any is Double || any is Float
}

private class JsonWholeNumberListConfigurationEntry(key: String, backend: JsonObject) : JsonListConfigurationEntry(key, backend) {
    override val type = EntryType.LIST_OF_WHOLE_NUMBERS
    override var currentValue: Any
        get() = if (this.jsonValue.isJsonArray) this.jsonValue.asJsonArray.decodeIntoList { it.asLong } else this.throwTypeMismatch()
        set(value) = this.backend.add(JSON_VALUE_MARKER, this.checkAndCast<Long>(value).encode())
    override fun isValidType(any: Any?) = any is Long || any is Int || any is Short || any is Byte
}

private class JsonStringListConfigurationEntry(key: String, backend: JsonObject) : JsonListConfigurationEntry(key, backend) {
    override val type = EntryType.LIST_OF_STRINGS
    override var currentValue: Any
        get() = if (this.jsonValue.isJsonArray) this.jsonValue.asJsonArray.decodeIntoList { it.asString } else this.throwTypeMismatch()
        set(value) = this.backend.add(JSON_VALUE_MARKER, this.checkAndCast<String>(value).encode())
    override fun isValidType(any: Any?) = any is String
}

private class JsonAnyListConfigurationEntry(key: String, backend: JsonObject) : JsonListConfigurationEntry(key, backend) {
    override val type = EntryType.LIST_OF_OBJECTS
    override var currentValue: Any
        get() = if (this.jsonValue.isJsonArray) this.jsonValue.asJsonArray.decodeIntoList(::decoder) else this.throwTypeMismatch()
        set(value) = this.backend.add(JSON_VALUE_MARKER, this.checkAndCast<Any>(value).encode())
    override fun isValidType(any: Any?) = true

    private fun decoder(ele: JsonElement): Any {
        val list = (this.decodedData[JSON_DCD_DEF] ?: throw JsonIllegalConfigurationEntryStructure("null default")) as List<*>
        if (list.isEmpty()) throw JsonIllegalConfigurationEntryStructure("Unable to deserialize any list without any sort of type information")
        val def = list.firstOrNull { it != null } ?: throw JsonIllegalConfigurationEntryStructure("Found null in a string")
        return prettyJsonWriter.fromJson(ele, def::class.java)
    }
}

private class JsonIllegalValueForEntryException(message: String) : Exception(message)
private class JsonIllegalConfigurationEntryType(message: String) : Exception(message)
private class JsonIllegalConfigurationEntryStructure(message: String) : Exception(message)
private class JsonConfigurationInvalidDefaultStructure(message: String, cause: Throwable? = null) : Exception(message, cause)

@Suppress("SpellCheckingInspection") private const val JSON_INTERNAL_MARKER = "hmsdqmzk"
private const val JSON_COMMENT_MARKER = "comment"
private const val JSON_LANG_KEY_MARKER = "language_key"
private const val JSON_CAT_MARKER = "categories"
private const val JSON_DOG_MARKER = "entries"
private const val JSON_VALUE_MARKER = "value"

private const val JSON_DCD_TYPE = "0"
private const val JSON_DCD_BOUNDS = "1"
private const val JSON_DCD_PROP = "2"
private const val JSON_DCD_DEF = "3"

private val rawJsonWriter = GsonBuilder().disableHtmlEscaping().serializeNulls().create()
private val prettyJsonWriter = GsonBuilder().disableHtmlEscaping().serializeNulls().setPrettyPrinting().create()

private fun JsonElement.wrapToCategory(key: String): Category = JsonConfigurationCategory(key, this as JsonObject)
private fun JsonElement.wrapToEntry(key: String) = (this as JsonObject).wrapToEntry(key)
private fun JsonObject.wrapToEntry(key: String) = this.wrapToEntry(key, this[JSON_INTERNAL_MARKER]!!.asString.decodeInternalData()[JSON_DCD_TYPE] as EntryType)
private fun JsonObject.wrapToEntry(key: String, type: EntryType): Entry = when (type) {
    EntryType.STRING -> JsonStringConfigurationEntry(key, this)
    EntryType.REAL_NUMBER -> JsonRealNumberConfigurationEntry(key, this)
    EntryType.WHOLE_NUMBER -> JsonWholeNumberConfigurationEntry(key, this)
    EntryType.BOOLEAN -> JsonBooleanConfigurationEntry(key, this)
    EntryType.OBJECT -> JsonAnyConfigurationEntry(key, this)
    EntryType.LIST_OF_STRINGS -> JsonStringListConfigurationEntry(key, this)
    EntryType.LIST_OF_REAL_NUMBERS -> JsonRealNumberListConfigurationEntry(key, this)
    EntryType.LIST_OF_WHOLE_NUMBERS -> JsonWholeNumberListConfigurationEntry(key, this)
    EntryType.LIST_OF_BOOLEANS -> JsonBooleanListConfigurationEntry(key, this)
    EntryType.LIST_OF_OBJECTS -> JsonAnyListConfigurationEntry(key, this)
}

// ======================== ENCODING ========================
// type;min^max;bit_field_markers;default
private fun ConfigurationEntryBuilder.encodeInternalData() =
        "${this.type.toTypeString()};${this.bounds().encode(this.type)};${this.properties().encode()};${this.default.encodeDefault(this.type)}"
private fun EntryType.toTypeString() = when (this) {
    EntryType.STRING -> "s"
    EntryType.REAL_NUMBER -> "r"
    EntryType.WHOLE_NUMBER -> "w"
    EntryType.BOOLEAN -> "b"
    EntryType.OBJECT -> "a"
    EntryType.LIST_OF_STRINGS -> "ss"
    EntryType.LIST_OF_REAL_NUMBERS -> "rr"
    EntryType.LIST_OF_WHOLE_NUMBERS -> "ww"
    EntryType.LIST_OF_BOOLEANS -> "bb"
    EntryType.LIST_OF_OBJECTS -> "aa"
}
private fun List<Boolean>.encode() = ((if (this[0]) 1 else 0) + (if (this[1]) 2 else 0) + (if (this[2]) 4 else 0)).toString(radix = 2)
private fun Pair<Any?, Any?>.encode(type: EntryType) =
        (if (this.first == null) "" else this.first!!.encodeDefault(type)) + (if (this.second == null) "" else "^${this.second!!.encodeDefault(type)}")
private fun Any.encodeDefault(type: EntryType): String = when (type) {
    EntryType.STRING -> this as String
    EntryType.REAL_NUMBER -> (if (this is Float) this.toDouble() else this as Double).toString()
    EntryType.WHOLE_NUMBER -> (when (this) {
        is Int -> this.toLong()
        is Short -> this.toLong()
        is Byte -> this.toLong()
        else -> this as Long
    }).toString()
    EntryType.BOOLEAN -> if (this as Boolean) "1" else "0"
    EntryType.OBJECT -> this.encodeAny()
    EntryType.LIST_OF_STRINGS -> (this as List<*>).encodeListDefault(EntryType.STRING)
    EntryType.LIST_OF_REAL_NUMBERS -> (this as List<*>).encodeListDefault(EntryType.REAL_NUMBER)
    EntryType.LIST_OF_WHOLE_NUMBERS -> (this as List<*>).encodeListDefault(EntryType.WHOLE_NUMBER)
    EntryType.LIST_OF_BOOLEANS -> (this as List<*>).encodeListDefault(EntryType.BOOLEAN)
    EntryType.LIST_OF_OBJECTS -> (this as List<*>).encodeListDefault(EntryType.OBJECT)
}
private fun List<*>.encodeListDefault(type: EntryType) =
        "[@${this.count()}@>${this.map { it!!.encodeDefault(type).replace(',', '§') }.joinToString(separator = ",") { it }}"
private fun Any.encodeAny() = "##${this::class.qualifiedName}##${rawJsonWriter.toJson(this)}"

private fun ConfigurationEntryBuilder.encodeValueForDefault(): JsonElement = this.default.encodeValue(this.type)
private fun Any.encodeValue(type: EntryType) = when (type) {
    EntryType.STRING -> JsonPrimitive(this as String)
    EntryType.REAL_NUMBER -> JsonPrimitive(if (this is Float) this.toDouble() else this as Double)
    EntryType.WHOLE_NUMBER -> JsonPrimitive(when (this) {
        is Int -> this.toLong()
        is Short -> this.toLong()
        is Byte -> this.toLong()
        else -> this as Long
    })
    EntryType.BOOLEAN -> JsonPrimitive(this as Boolean)
    EntryType.OBJECT -> this.encodeAnyValue()
    EntryType.LIST_OF_STRINGS -> this.encodeListValue(EntryType.STRING)
    EntryType.LIST_OF_REAL_NUMBERS -> this.encodeListValue(EntryType.REAL_NUMBER)
    EntryType.LIST_OF_WHOLE_NUMBERS -> this.encodeListValue(EntryType.WHOLE_NUMBER)
    EntryType.LIST_OF_BOOLEANS -> this.encodeListValue(EntryType.BOOLEAN)
    EntryType.LIST_OF_OBJECTS -> this.encodeListValue(EntryType.OBJECT)
}
private fun Any.encodeAnyValue(): JsonObject = prettyJsonWriter.fromJson(prettyJsonWriter.toJson(this), JsonObject::class.java)
private fun Any.encodeListValue(type: EntryType) = (this as List<*>).encodeListValue(type)
private fun List<*>.encodeListValue(type: EntryType): JsonElement {
    val array = JsonArray()
    this.asSequence()
            .map { it?.encodeValue(type) ?: throw JsonIllegalConfigurationEntryStructure("Unable to serialize a list that contains nulls") }
            .forEach { array.add(it) }
    return array
}

// ======================== DECODING ========================
private fun String.decodeInternalData(): Map<String, Any> {
    val (t, b, p, d) = this.split(";", limit = 4)
    val type = t.toEntryType()
    val bounds = b.decodePair(type)
    val props = p.decodeProps()
    val def = d.decodeDefault(type)
    return mapOf(JSON_DCD_TYPE to type, JSON_DCD_BOUNDS to bounds, JSON_DCD_PROP to props, JSON_DCD_DEF to def)
}
private fun String.toEntryType() = when (this) {
    "s" -> EntryType.STRING
    "r" -> EntryType.REAL_NUMBER
    "w" -> EntryType.WHOLE_NUMBER
    "b" -> EntryType.BOOLEAN
    "a" -> EntryType.OBJECT
    "ss" -> EntryType.LIST_OF_STRINGS
    "rr" -> EntryType.LIST_OF_REAL_NUMBERS
    "ww" -> EntryType.LIST_OF_WHOLE_NUMBERS
    "bb" -> EntryType.LIST_OF_BOOLEANS
    "aa" -> EntryType.LIST_OF_OBJECTS
    else -> throw JsonIllegalConfigurationEntryType("The string '$this' doesn't match any known JSON Configuration type")
}
private fun String.decodePair(type: EntryType): Pair<Any?, Any?> {
    val hasMax = this.indexOf('^') != -1
    val hasOnlyMax = hasMax && this.indexOf('^') == 0
    val hasMin = this.isNotEmpty()
    val hasOnlyMin = hasMin && !hasMax
    val max = if (hasOnlyMin || !hasMax) null else this.substring(startIndex = this.indexOf('^') + 1, endIndex = this.length).decodeDefault(type)
    val min = if (hasOnlyMax || !hasMin) null else this.substring(startIndex = 0, endIndex = if (hasMax) this.indexOf('^') else this.length).decodeDefault(type)
    return Pair(min, max)
}
private fun String.decodeProps(): List<Boolean> {
    val value = this.toInt(radix = 2)
    val restart = value and 1 == 0
    val reload = value and 2 == 0
    val slider = value and 4 == 0
    return listOf(restart, reload, slider)
}
private fun String.decodeDefault(type: EntryType) = try {
    this.tryDecodeDefault(type)
} catch (e: Exception) {
    throw JsonConfigurationInvalidDefaultStructure("$this is not valid for $type", e)
}
private fun String.tryDecodeDefault(type: EntryType): Any = when (type) {
    EntryType.STRING -> this
    EntryType.REAL_NUMBER -> this.toDouble()
    EntryType.WHOLE_NUMBER -> this.toLong()
    EntryType.BOOLEAN -> when (this) {
        "1" -> true
        "0" -> false
        else -> throw JsonConfigurationInvalidDefaultStructure("The value $this is not a valid boolean representation")
    }
    EntryType.OBJECT -> this.decodeAny()
    EntryType.LIST_OF_STRINGS -> this.decodeList(EntryType.STRING)
    EntryType.LIST_OF_REAL_NUMBERS -> this.decodeList(EntryType.REAL_NUMBER)
    EntryType.LIST_OF_WHOLE_NUMBERS -> this.decodeList(EntryType.WHOLE_NUMBER)
    EntryType.LIST_OF_BOOLEANS -> this.decodeList(EntryType.BOOLEAN)
    EntryType.LIST_OF_OBJECTS -> this.decodeList(EntryType.OBJECT)
}
private fun String.decodeAny(): Any {
    val (empty, className, content) = this.split("##", limit = 3)
    if (empty.isNotEmpty()) throw JsonConfigurationInvalidDefaultStructure("The value $this is not a valid Any type")
    return rawJsonWriter.fromJson(content, Class.forName(className))
}
private fun String.decodeList(type: EntryType): Any {
    if (!this.startsWith("[@")) throw JsonConfigurationInvalidDefaultStructure("The value $this is not a valid List type")
    val count = this.substring(startIndex = 2, endIndex = this.indexOf("@>")).toInt()
    val content = this.substring(startIndex = this.indexOf("@>") + 2, endIndex = this.length)
    val decodedList = content.split(",").map { it.replace('§', ',') }.map { it.tryDecodeDefault(type) }
    if (decodedList.count() != count) throw JsonConfigurationInvalidDefaultStructure("$this is not a list of the expected length")
    return decodedList
}
private fun JsonObject.decode(default: Any?): Any =
        prettyJsonWriter.fromJson(this, (default ?: throw JsonConfigurationInvalidDefaultStructure("null default"))::class.java)
private fun <T> JsonArray.decodeIntoList(decoder: (JsonElement) -> T): List<T> {
    val list = mutableListOf<T>()
    this.forEach { list.add(decoder(it)) }
    return list.toList()
}
