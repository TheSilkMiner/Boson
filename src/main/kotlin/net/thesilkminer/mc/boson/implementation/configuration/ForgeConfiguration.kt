@file:JvmName("FC")

package net.thesilkminer.mc.boson.implementation.configuration

import com.google.gson.GsonBuilder
import net.minecraftforge.common.config.ConfigCategory
import net.minecraftforge.common.config.Property
import net.minecraftforge.common.config.Configuration as ForgeConfig
import net.thesilkminer.mc.boson.api.configuration.Category
import net.thesilkminer.mc.boson.api.configuration.Configuration
import net.thesilkminer.mc.boson.api.configuration.ConfigurationBuilder
import net.thesilkminer.mc.boson.api.configuration.ConfigurationCategoryBuilder
import net.thesilkminer.mc.boson.api.configuration.ConfigurationEntryBuilder
import net.thesilkminer.mc.boson.api.configuration.ConfigurationFormat
import net.thesilkminer.mc.boson.api.configuration.Entry
import net.thesilkminer.mc.boson.api.configuration.EntryType

internal class ForgeConfiguration(builder: ConfigurationBuilder) : Configuration {
    companion object {
        const val FORGE_CONFIGURATION_FILE_EXTENSION = "cfg"
    }

    private val backend by lazy { ForgeConfig(this.location.toFile()).apply { this.populate(builder) } }

    override val format = ConfigurationFormat.FORGE_CONFIG
    override val targetDistribution = builder.targetDistribution
    override val owner = builder.owner
    override val name = builder.name
    override val location = builder.constructPath(this.format)
    override val categories: List<Category> = this.backend.categoryNames.filter { !it.representsSubCategory() }.map { this.backend.getCategory(it) }.map { it.wrap() }

    override fun save() = this.backend.save()
    override fun load() = this.backend.load()

    override operator fun get(category: String, vararg subCategories: String) = this.backend.getCategory(category).wrap().let {
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

    override fun toString(): String = "ForgeConfiguration(format=${this.format}, owner='${this.owner}', name='${this.name}', categories=${this.categories})"

    private fun ForgeConfig.populate(builder: ConfigurationBuilder) {
        fun ForgeConfig.populateCategory(category: ConfigurationCategoryBuilder, parent: ConfigCategory?) {
            // Create the category
            val forgeCategory = this.getCategory(ConfigCategory.getQualifiedName(category.name, parent))
            forgeCategory.comment = category.comment
            forgeCategory.setLanguageKey(category.languageKey)

            // Add this category entries
            category.entries().forEach { entry ->
                forgeCategory[entry.name] = entry.toProperty(forgeCategory[entry.name])
            }

            // And now add child categories
            category.subCategories().forEach { this.populateCategory(it, forgeCategory) }
        }

        builder.categories().forEach { this.populateCategory(it, null) }

        // Let's save it so we have a default ready to start with
        this.save()
    }

    private fun ConfigurationEntryBuilder.toProperty(previousProperty: Property?) = when (this.type) {
        EntryType.LIST_OF_REAL_NUMBERS, EntryType.LIST_OF_OBJECTS, EntryType.LIST_OF_STRINGS, EntryType.LIST_OF_WHOLE_NUMBERS,
        EntryType.LIST_OF_BOOLEANS -> this.toPropertyArray(previousProperty)
        else -> this.toPropertyString(previousProperty)
    }
    private fun ConfigurationEntryBuilder.toPropertyString(previousProperty: Property?) = this.toPropertyObject(previousProperty) {
        Property(this.name, this.default.toString(), this.type.convertToForgeType())
    }
    private fun ConfigurationEntryBuilder.toPropertyArray(previousProperty: Property?) = this.toPropertyObject(previousProperty) {
        Property(
                this.name,
                (this.default as List<*>).map { it.toString() }.toTypedArray(),
                this.type.convertToForgeType()
        )
    }
    private fun ConfigurationEntryBuilder.toPropertyObject(previousProperty: Property?, provider: () -> Property): Property {
        val property = provider()
        if (!property.isList) property.setDefaultValue(this.default.toString())
        if (previousProperty != null) {
            if (property.isList) property.set(previousProperty.stringList)
            else property.set(previousProperty.string)
        }
        property.comment = this.comment
        property.languageKey = this.languageKey
        val properties = this.properties()
        property.setRequiresMcRestart(properties[0])
        property.setRequiresWorldRestart(properties[1])
        property.setHasSlidingControl(properties[2])
        val bounds = this.bounds()
        if (bounds.first != null) {
            when (bounds.first) {
                is Int -> property.setMinValue(bounds.first as Int)
                is Double -> property.setMinValue(bounds.first as Double)
                else -> throw ForgeUnsupportedBoundsOnEntryException("Unable to set minimum bound ${bounds.first} because it is not an Int or a Double: ${bounds.first!!::class}")
            }
        }
        if (bounds.second != null) {
            when (bounds.second) {
                is Int -> property.setMaxValue(bounds.second as Int)
                is Double -> property.setMaxValue(bounds.second as Double)
                else -> throw ForgeUnsupportedBoundsOnEntryException("Unable to set maximum bound ${bounds.second} because it is not an Int or a Double: ${bounds.second!!::class}")
            }
        }
        return property
    }
    private fun EntryType.convertToForgeType() = when (this) {
        EntryType.STRING, EntryType.LIST_OF_STRINGS -> Property.Type.STRING
        EntryType.REAL_NUMBER, EntryType.LIST_OF_REAL_NUMBERS -> Property.Type.DOUBLE
        EntryType.WHOLE_NUMBER, EntryType.LIST_OF_WHOLE_NUMBERS -> Property.Type.INTEGER
        EntryType.BOOLEAN, EntryType.LIST_OF_BOOLEANS -> Property.Type.BOOLEAN
        EntryType.OBJECT, EntryType.LIST_OF_OBJECTS -> Property.Type.STRING // Requires custom serialization techniques
    }
    private fun String.representsSubCategory(): Boolean = this.contains(ForgeConfig.CATEGORY_SPLITTER)
    private fun ConfigCategory.wrap(): Category = ForgeConfigurationCategory(this)
}

private class ForgeConfigurationCategory(private val forgeCategory: ConfigCategory) : Category {
    override val name: String = this.forgeCategory.name
    override val comment: String = this.forgeCategory.comment
    override val languageKey: String = this.forgeCategory.languagekey
    override val categories: List<Category> = this.forgeCategory.children.toList().map { ForgeConfigurationCategory(it) }
    override val entries: List<Entry> = this.forgeCategory.values.toList().map { it.wrap() }

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

    override fun toString() = "ForgeConfigurationCategory(name='${this.name}', comment='${this.comment}', " +
            "languageKey='${this.languageKey}', subcategories=${this.categories}, entries=${this.entries})"

    private fun Property.wrap(): Entry = when (this.type) {
        Property.Type.STRING -> this.wrapString()
        Property.Type.INTEGER -> if (this.isList) ForgeWholeNumberListConfigurationEntry(this) else ForgeWholeNumberConfigurationEntry(this)
        Property.Type.BOOLEAN -> if (this.isList) ForgeBooleanListConfigurationEntry(this) else ForgeBooleanConfigurationEntry(this)
        Property.Type.DOUBLE -> if (this.isList) ForgeRealNumberListConfigurationEntry(this) else ForgeRealNumberConfigurationEntry(this)
        else -> throw ForgeUnsupportedPropertyTypeException("Unable to convert from native type ${this.type} to API type")
    }
    private fun Property.wrapString() = when {
        this.string.startsWith(ForgeObjectConfigurationEntry.OBJECT_MARKER) -> this.wrapObjectString()
        this.isList -> ForgeStringListConfigurationEntry(this)
        else -> ForgeStringConfigurationEntry(this)
    }
    private fun Property.wrapObjectString() = if (this.isList) ForgeAnyListConfigurationEntry(this) else ForgeObjectConfigurationEntry(this)
}

private sealed class ForgeConfigurationEntry(private val forgeEntry: Property) : Entry {
    final override val name: String = this.forgeEntry.name
    abstract override val type: EntryType
    final override val comment: String = this.forgeEntry.comment
    final override val languageKey: String = this.forgeEntry.languageKey
    final override val default: Any = this.forgeEntry.default
    final override val requiresMcRestart: Boolean = this.forgeEntry.requiresMcRestart()
    final override val requiresWorldReload: Boolean = this.forgeEntry.requiresWorldRestart()
    final override val hasSlider: Boolean = this.forgeEntry.hasSlidingControl()
    abstract override val bounds: Pair<Any?, Any?>
    abstract override var currentValue: Any

    override fun toString() = "ForgeConfigurationEntry[${this::class.simpleName}](name='${this.name}', type=${this.type}, comment='${this.comment}', " +
            "languageKey='${this.languageKey}', default=${this.default}, requiresMcRestart=${this.requiresMcRestart}, requiresWorldReload=${this.requiresWorldReload}, " +
            "hasSlider=${this.hasSlider}, bounds=${this.bounds}, currentValue=${this.currentValue})"

    protected fun throwTypeMismatch(): Nothing =
            throw ForgeIllegalValueForEntryException("This entry does not store a value that matches the type ${this.type}")
    protected fun throwTypeMismatch(attempt: Any): Nothing =
            throw ForgeIllegalValueForEntryException("Unable to assign the value '$attempt' to a property of type ${this.type}")
}

private class ForgeBooleanConfigurationEntry(private val forgeEntry: Property) : ForgeConfigurationEntry(forgeEntry) {
    override val type: EntryType = EntryType.BOOLEAN
    override val bounds: Pair<Any?, Any?> = Pair(null, null)
    override var currentValue: Any
        get() = if (this.forgeEntry.isBooleanValue) this.forgeEntry.boolean else this.throwTypeMismatch()
        set(value) = if (value is Boolean) this.forgeEntry.set(value) else this.throwTypeMismatch(value)
}

private class ForgeRealNumberConfigurationEntry(private val forgeEntry: Property) : ForgeConfigurationEntry(forgeEntry) {
    override val type: EntryType = EntryType.REAL_NUMBER
    override val bounds: Pair<Any?, Any?> = Pair(forgeEntry.minValue.toMinBoundOrNull(), forgeEntry.maxValue.toMaxBoundOrNull())
    override var currentValue: Any
        get() = if (this.forgeEntry.isDoubleValue) this.forgeEntry.double.checkBounded() else this.throwTypeMismatch()
        set(value) = when (value) {
            is Double -> this.forgeEntry.set(value.checkBounded())
            is Float -> this.forgeEntry.set(value.toDouble().checkBounded())
            else -> this.throwTypeMismatch(value)
        }

    private fun Double.checkBounded(): Double {
        val minBound = this@ForgeRealNumberConfigurationEntry.bounds.first as Double? ?: -Double.MAX_VALUE
        val maxBound = this@ForgeRealNumberConfigurationEntry.bounds.second as Double? ?: Double.MAX_VALUE
        if (minBound > this || maxBound < this) throw ForgeIllegalValueForEntryException("Value $this was not between bounds $minBound and $maxBound")
        return this
    }
    private fun String?.toMinBoundOrNull() = if (this == null || this.isBlank() || this == Integer.MIN_VALUE.toString()) null else this.toDoubleOrNull()
    private fun String?.toMaxBoundOrNull() = if (this == null || this.isBlank() || this == Integer.MAX_VALUE.toString()) null else this.toDoubleOrNull()
}

private class ForgeWholeNumberConfigurationEntry(private val forgeEntry: Property) : ForgeConfigurationEntry(forgeEntry) {
    override val type: EntryType = EntryType.WHOLE_NUMBER
    override val bounds: Pair<Any?, Any?> = Pair(forgeEntry.minValue.toMinBoundOrNull(), forgeEntry.maxValue.toMaxBoundOrNull())
    override var currentValue: Any
        get() = if (this.forgeEntry.isLongValue) this.forgeEntry.long.checkBounded() else this.throwTypeMismatch()
        set(value) = when (value) {
            is Long -> this.forgeEntry.set(value.checkBounded())
            is Int -> this.forgeEntry.set(value.toLong().checkBounded())
            is Short -> this.forgeEntry.set(value.toLong().checkBounded())
            is Byte -> this.forgeEntry.set(value.toLong().checkBounded())
            else -> this.throwTypeMismatch(value)
        }

    private fun Long.checkBounded(): Long {
        val minBound = this@ForgeWholeNumberConfigurationEntry.bounds.first as Long? ?: Long.MIN_VALUE
        val maxBound = this@ForgeWholeNumberConfigurationEntry.bounds.second as Long? ?: Long.MAX_VALUE
        if (minBound > this || maxBound < this) throw ForgeIllegalValueForEntryException("Value $this was not between bounds $minBound and $maxBound")
        return this
    }
    private fun String?.toMinBoundOrNull() = if (this == null || this.isBlank() || this == Integer.MIN_VALUE.toString()) null else this.toLongOrNull()
    private fun String?.toMaxBoundOrNull() = if (this == null || this.isBlank() || this == Integer.MAX_VALUE.toString()) null else this.toLongOrNull()
}

private class ForgeStringConfigurationEntry(private val forgeEntry: Property) : ForgeConfigurationEntry(forgeEntry) {
    override val type: EntryType = EntryType.STRING
    override val bounds: Pair<Any?, Any?> = Pair(null, null)
    override var currentValue: Any
        get() = this.forgeEntry.string
        set(value) = if (value is String) this.forgeEntry.set(value) else this.throwTypeMismatch(value)
}

private class ForgeObjectConfigurationEntry(private val forgeEntry: Property) : ForgeConfigurationEntry(forgeEntry) {
    companion object {
        const val OBJECT_MARKER = "__\$93925925\$a290bGluLkFueQ==\$"
    }

    override val type: EntryType = EntryType.OBJECT
    override val bounds: Pair<Any?, Any?> = Pair(null, null)
    override var currentValue: Any
        get() = if (this.forgeEntry.string.startsWith(OBJECT_MARKER)) this.forgeEntry.string.toAny() else this.throwTypeMismatch()
        set(value) = this.forgeEntry.set(value.toConfigurationString())
}

private sealed class ForgeListConfigurationEntry(forgeEntry: Property) : ForgeConfigurationEntry(forgeEntry) {
    abstract override val type: EntryType
    final override val bounds: Pair<Any?, Any?> = Pair(null, null)
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

private class ForgeBooleanListConfigurationEntry(private val forgeEntry: Property) : ForgeListConfigurationEntry(forgeEntry) {
    override val type: EntryType = EntryType.LIST_OF_BOOLEANS
    override var currentValue: Any
        get() = if (this.forgeEntry.isBooleanList) this.forgeEntry.booleanList else this.throwTypeMismatch()
        set(value) = this.forgeEntry.set(this.checkAndCast<Boolean>(value).toTypedArray().toBooleanArray())
    override fun isValidType(any: Any?) = any is Boolean
}

private class ForgeWholeNumberListConfigurationEntry(private val forgeEntry: Property) : ForgeListConfigurationEntry(forgeEntry) {
    override val type: EntryType = EntryType.LIST_OF_WHOLE_NUMBERS
    override var currentValue: Any
        get() = if (this.forgeEntry.isIntList) this.forgeEntry.intList else this.throwTypeMismatch()
        set(value) = this.forgeEntry.set(this.checkAndCast<Int>(value).toTypedArray().toIntArray())
    override fun isValidType(any: Any?): Boolean = any is Int || any is Long || any is Short || any is Byte
}

private class ForgeRealNumberListConfigurationEntry(private val forgeEntry: Property) : ForgeListConfigurationEntry(forgeEntry) {
    override val type: EntryType = EntryType.LIST_OF_REAL_NUMBERS
    override var currentValue: Any
        get() = if (this.forgeEntry.isDoubleList) this.forgeEntry.doubleList else this.throwTypeMismatch()
        set(value) = this.forgeEntry.set(this.checkAndCast<Double>(value).toTypedArray().toDoubleArray())
    override fun isValidType(any: Any?): Boolean = any is Double || any is Float
}

private class ForgeStringListConfigurationEntry(private val forgeEntry: Property) : ForgeListConfigurationEntry(forgeEntry) {
    override val type: EntryType = EntryType.LIST_OF_STRINGS
    override var currentValue: Any
        get() = if (this.forgeEntry.isList) this.forgeEntry.stringList else this.throwTypeMismatch()
        set(value) = this.forgeEntry.set(this.checkAndCast<String>(value).toTypedArray())
    override fun isValidType(any: Any?): Boolean = any is String
}

private class ForgeAnyListConfigurationEntry(private val forgeEntry: Property) : ForgeListConfigurationEntry(forgeEntry) {
    override val type: EntryType = EntryType.LIST_OF_OBJECTS
    override var currentValue: Any
        get() = if (this.forgeEntry.isList) this.forgeEntry.stringList.map { it.toAny() } else this.throwTypeMismatch()
        set(value) = this.forgeEntry.set(this.checkAndCast<Any>(value).map { it.toConfigurationString() }.toTypedArray())
    override fun isValidType(any: Any?): Boolean = true
}

private class ForgeUnsupportedBoundsOnEntryException(message: String) : Exception(message)
private class ForgeIllegalValueForEntryException(message: String) : Exception(message)
private class ForgeUnsupportedPropertyTypeException(message: String) : Exception(message)

private val json = GsonBuilder().serializeNulls().disableHtmlEscaping().create()
private fun Any.toConfigurationString(): String {
    val anyClass = this::class
    val jsonString = json.toJson(this)
    return "${ForgeObjectConfigurationEntry.OBJECT_MARKER}$anyClass\$serializedObject;1=$jsonString"
}
private fun String.toAny() {
    if (!this.startsWith(ForgeObjectConfigurationEntry.OBJECT_MARKER)) throw ForgeIllegalValueForEntryException("Cannot convert non-Any string back to Any")
    val (`class`, `object`) = this.removePrefix(ForgeObjectConfigurationEntry.OBJECT_MARKER).split("\$", limit = 2)
    if (!`object`.startsWith("serializedObject;1=")) throw ForgeIllegalValueForEntryException("Invalid format and/or version for Any string $this")
    json.fromJson(`object`.removePrefix("serializedObject;1="), Class.forName(`class`))
}
