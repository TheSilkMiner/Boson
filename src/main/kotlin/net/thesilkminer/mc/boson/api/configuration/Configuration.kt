package net.thesilkminer.mc.boson.api.configuration

import net.thesilkminer.mc.boson.api.distribution.Distribution
import java.nio.file.Path

interface Configuration {
    val format: ConfigurationFormat
    val targetDistribution: Distribution? //TODO("Make it count")
    val owner: String
    val name: String
    val location: Path
    val categories: List<Category>

    fun save()
    fun load()

    operator fun get(category: String, vararg subCategories: String): Category
}
