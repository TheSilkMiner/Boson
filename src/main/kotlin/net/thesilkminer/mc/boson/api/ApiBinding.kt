@file:JvmName("ApiBindings")

package net.thesilkminer.mc.boson.api

import net.thesilkminer.mc.boson.api.configuration.Category
import net.thesilkminer.mc.boson.api.configuration.Configuration
import net.thesilkminer.mc.boson.api.configuration.ConfigurationBuilder
import net.thesilkminer.mc.boson.api.configuration.ConfigurationFormat
import net.thesilkminer.mc.boson.api.distribution.Distribution
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.loader.ContextKey
import net.thesilkminer.mc.boson.api.loader.Loader
import net.thesilkminer.mc.boson.api.loader.LoaderBuilder
import net.thesilkminer.mc.boson.api.locale.Color
import net.thesilkminer.mc.boson.api.locale.Readability
import net.thesilkminer.mc.boson.api.locale.Style
import net.thesilkminer.mc.boson.api.log.L
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ServiceLoader
import kotlin.reflect.KClass

private val l = L("Boson API", "API Bindings")

val bosonApi by lazy {
    loadWithService(BosonApi::class) {
        object : BosonApi {
            init {
                l.bigError("No API binding found! Replacing with dummy implementation")
            }

            override val configurationDirectory: Path get() = Paths.get(".")

            override fun buildConfiguration(builder: ConfigurationBuilder) = object : Configuration {
                override val format: ConfigurationFormat = ConfigurationFormat.DEFAULT
                override val owner: String = "dummy"
                override val name: String = "dummy"
                override val location: Path = Paths.get(".")
                override val categories: List<Category> = listOf()
                override fun save() = Unit
                override fun load() = Unit
                override operator fun get(category: String, vararg subCategories: String): Category = error("Category '$category' does not exist")
            }

            override val currentDistribution: Distribution = Distribution.DEDICATED_SERVER

            override fun localizeAndFormat(message: String, color: Color, style: Style, readability: Readability, vararg arguments: Any?) = message

            override fun constructNameSpacedString(nameSpace: String?, path: String) = object : NameSpacedString {
                override val nameSpace = nameSpace ?: "null"
                override val path = path
                override fun compareTo(other: NameSpacedString) = this.nameSpace.compareTo(other.nameSpace).let { return@let if (it == 0) 0 else this.path.compareTo(other.path) }
            }

            override fun <T : Any> createLoaderContextKey(name: String, type: KClass<T>): ContextKey<T> = object : ContextKey<T> {
                override val name: String = name
                override val type: KClass<T> = type
            }

            override fun buildLoader(builder: LoaderBuilder) = object : Loader {
                override fun load() = Unit
            }
        }
    }
}

interface BosonApi {
    val configurationDirectory: Path
    fun buildConfiguration(builder: ConfigurationBuilder): Configuration

    val currentDistribution: Distribution

    fun localizeAndFormat(message: String, color: Color, style: Style, readability: Readability, vararg arguments: Any?): String

    fun constructNameSpacedString(nameSpace: String?, path: String): NameSpacedString

    fun <T : Any> createLoaderContextKey(name: String, type: KClass<T>): ContextKey<T>
    fun buildLoader(builder: LoaderBuilder): Loader
}

private fun <T : Any> loadWithService(lookUpInterface: KClass<T>, defaultProvider: () -> T) : T {
    fun <T: Any> load(lookUpInterface: KClass<T>, defaultProvider: () -> T): T {
        val foundImplementations = ServiceLoader.load(lookUpInterface.java).asSequence()

        if (foundImplementations.count() > 1) {
            l.warn("Found multiple implementations for ${lookUpInterface.simpleName} API Bindings: trying to find our own")
            val bosonImpl = foundImplementations.find { it::class.java.name.contains("mc.boson.implementation") }
            if (bosonImpl != null) return bosonImpl
            l.bigWarn("Unable to find Boson implementation for ${lookUpInterface.simpleName} API Binding!")
            return foundImplementations.first()
        }

        val impl = foundImplementations.firstOrNull()
        if (impl == null) {
            l.bigError("No API binding found! Replacing with dummy API implementation.\nNote that this may lead to future errors")
            return defaultProvider()
        }
        return impl
    }

    return load(lookUpInterface, defaultProvider).apply {
        l.info("Using ${this::class.qualifiedName} as API Binding for Boson class ${lookUpInterface.qualifiedName}")
    }
}
