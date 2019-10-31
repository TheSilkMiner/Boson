@file:JvmName("ApiBindings")

package net.thesilkminer.mc.boson.api

import net.thesilkminer.mc.boson.api.configuration.Category
import net.thesilkminer.mc.boson.api.configuration.Configuration
import net.thesilkminer.mc.boson.api.configuration.ConfigurationBuilder
import net.thesilkminer.mc.boson.api.configuration.ConfigurationFormat
import net.thesilkminer.mc.boson.api.configuration.Entry
import net.thesilkminer.mc.boson.api.distribution.Distribution
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
                override operator fun get(category: String): Category = error("Category '$category' does not exist")
                override operator fun get(category: String, entry: String): Entry = error("Entry '$entry' does not exist in category '$category'")
            }

            override val currentDistribution: Distribution = Distribution.DEDICATED_SERVER
        }
    }
}

interface BosonApi {
    val configurationDirectory: Path
    fun buildConfiguration(builder: ConfigurationBuilder): Configuration

    val currentDistribution: Distribution
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
