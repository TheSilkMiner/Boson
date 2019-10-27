package net.thesilkminer.mc.boson.api.configuration

import java.nio.file.Path

interface Configuration {
    val format: ConfigurationFormat
    val owner: String
    val name: String
    val location: Path
    val categories: List<Category>

    fun save()
    fun load()

    operator fun get(category: String): Category
    operator fun get(category: String, entry: String): Entry
}
