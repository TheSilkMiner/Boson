@file:JvmName("ConfigurationFactory")

package net.thesilkminer.mc.boson.api.configuration

import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.distribution.Distribution

@DslMarker
private annotation class ConfigurationDsl

@ConfigurationDsl
class ConfigurationBuilder {
    lateinit var owner: String
    lateinit var name: String
    var type = ConfigurationFormat.DEFAULT
    var targetDistribution = null as Distribution?

    private val categories = mutableListOf<ConfigurationCategoryBuilder>()

    fun categories(block: ConfigurationCategories.() -> Unit) {
        this.categories.addAll(ConfigurationCategories().apply(block))
    }

    fun categories() = listOf(*this.categories.toTypedArray())
    fun build() = bosonApi.buildConfiguration(this)
}

@ConfigurationDsl
class ConfigurationCategories: ArrayList<ConfigurationCategoryBuilder>() {
    operator fun String.invoke(block: ConfigurationCategoryBuilder.() -> Unit = {}) {
        this@ConfigurationCategories.add(ConfigurationCategoryBuilder(this).apply(block))
    }
}

@ConfigurationDsl
class ConfigurationCategoryBuilder(val name: String) {
    var comment = ""
    var languageKey = ""

    private val entries = mutableListOf<ConfigurationEntryBuilder>()
    private val subCategories = mutableListOf<ConfigurationCategoryBuilder>()

    fun entries(block: ConfigurationEntries.() -> Unit) {
        this.entries.addAll(ConfigurationEntries().apply(block))
    }

    fun subCategories(block: ConfigurationCategories.() -> Unit) {
        this.subCategories.addAll(ConfigurationCategories().apply(block))
    }

    fun entries() = listOf(*this.entries.toTypedArray())
    fun subCategories() = listOf(*this.subCategories.toTypedArray())
}

@ConfigurationDsl
class ConfigurationEntries : ArrayList<ConfigurationEntryBuilder>() {
    operator fun String.invoke(type: EntryType, block: ConfigurationEntryBuilder.() -> Unit = {}) {
        this@ConfigurationEntries.add(ConfigurationEntryBuilder(this, type).apply(block))
    }
}

@ConfigurationDsl
class ConfigurationEntryBuilder(val name: String, val type: EntryType) {
    var comment = ""
    var languageKey = ""
    var default = type.default

    private var bounds: Pair<Any?, Any?> = Pair(null, null)

    private var requiresRestart = false
    private var requiresReload = false

    private var hasSlider = false

    fun <T> bounds(min: T? = null, max: T? = null) {
        check(this.bounds.first == null && this.bounds.second == null) { "Bounds were already set" }
        this.bounds = Pair(min, max)
    }

    fun requiresGameRestart() {
        this.requiresRestart = true
    }

    fun requiresWorldReload() {
        this.requiresReload = true
    }

    fun addSlider() {
        check(bounds.first != null && bounds.second != null) { "Unable to set slider with incomplete bounds" }
        this.hasSlider = true
    }

    fun properties(): List<Boolean> = listOf(this.requiresRestart, this.requiresReload, this.hasSlider)
    fun bounds() = this.bounds
}

fun configuration(block: ConfigurationBuilder.() -> Unit) = ConfigurationBuilder().apply(block).build()
