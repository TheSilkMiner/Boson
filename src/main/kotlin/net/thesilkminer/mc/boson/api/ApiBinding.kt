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

@file:JvmName("ApiBindings")

package net.thesilkminer.mc.boson.api

import net.minecraftforge.fml.common.eventhandler.EventBus
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.api.communication.Message
import net.thesilkminer.mc.boson.api.communication.MessageHandler
import net.thesilkminer.mc.boson.api.communication.MessageHandlerRegistry
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityProvider
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityProviderRegistry
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
import net.thesilkminer.mc.boson.api.registry.DeferredRegister
import net.thesilkminer.mc.boson.api.registry.RegistryObject
import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.api.tag.TagRegistry
import net.thesilkminer.mc.boson.api.tag.TagType
import org.jetbrains.annotations.ApiStatus
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
                override val format = ConfigurationFormat.DEFAULT
                override val targetDistribution: Distribution? = null
                override val owner = "dummy"
                override val name = "dummy"
                override val location = Paths.get(".")
                override val categories = listOf<Category>()
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
                override val name = name
                override val type = type
            }

            override fun buildLoader(builder: LoaderBuilder) = object : Loader {
                override fun load() = Unit
            }

            override val tagRegistry = object: TagRegistry {
                private val EMPTY_TAG = object: Tag<Any> {
                    override val name = NameSpacedString("empty") // Why a recursive problem?
                    override val type = object: TagType<Any> {
                        override val type = Any::class
                        override val directoryName = "null"
                        override val toElement = { _: NameSpacedString -> Any() }
                        override val equalityEvaluator: (Any, Any) -> Boolean = { a: Any, b: Any -> a == b }
                    }
                    override val elements = setOf<Any>()
                    override fun add(elements: Set<Any>) = Unit
                    override fun addFrom(other: Tag<out Any>) = Unit
                    override fun replace(elements: Set<Any>) = Unit
                    override fun replaceWith(other: Tag<out Any>) = Unit
                    override fun remove(elements: Set<Any>) = Unit
                    override fun removeFrom(other: Tag<out Any>) = Unit
                    override fun clear() = Unit
                }

                override fun <T : Any> findAllTagsOf(type: TagType<T>) = listOf<Tag<T>>()
                override fun <T : Any> findTag(type: TagType<T>, name: NameSpacedString) = EMPTY_TAG.uncheckedCast<Tag<T>>()
                override fun <T : Any> findFor(target: T, type: TagType<T>) = listOf<Tag<T>>()

                override val isFrozen = false
            }

            override fun <T : Any> createTagType(type: KClass<out T>, directoryName: String, toElement: (NameSpacedString) -> T, equalityEvaluator: (T, T) -> Boolean) =
                    object : TagType<T> {
                        override val type = type
                        override val directoryName = directoryName
                        override val toElement = toElement
                        override val equalityEvaluator: (T, T) -> Boolean = equalityEvaluator
                    }

            override fun <T : Any> createTag(tagType: TagType<T>, name: NameSpacedString, vararg initialElements: T) = object : Tag<T> {
                override val name = name
                override val type = tagType
                override val elements = setOf<T>()
                override fun add(elements: Set<T>) = Unit
                override fun addFrom(other: Tag<out T>) = Unit
                override fun replace(elements: Set<T>) = Unit
                override fun replaceWith(other: Tag<out T>) = Unit
                override fun remove(elements: Set<T>) = Unit
                override fun removeFrom(other: Tag<out T>) = Unit
                override fun clear() = Unit
            }

            override fun <T : Any> findTagType(name: String) = null as TagType<T>?

            override val compatibilityProviderRegistry = object : CompatibilityProviderRegistry {
                override fun <T : CompatibilityProvider> registerProvider(provider: KClass<out T>) = Unit
                override fun findAllProviders() = sequenceOf<CompatibilityProvider>()
                override fun <T : CompatibilityProvider> findProviders(provider: KClass<out T>) = sequenceOf<T>()
            }

            override val messageHandlerRegistry = object: MessageHandlerRegistry {
                override fun register(receiver: String, handler: MessageHandler) = Unit
                override fun getHandlersFor(receiver: String) = sequenceOf<MessageHandler>()
            }

            override fun dispatchMessageTo(receiver: String, message: Message<*>) = Unit

            override fun getDatabasePathFor(owner: String): Path = this.configurationDirectory
        }
    }
}

val experimentalBosonApi by lazy {
    loadWithService(ExperimentalBosonApi::class) {
        object : ExperimentalBosonApi {
            init {
                l.bigError("No API binding found! Replacing with dummy implementation")
            }

            override fun <T : IForgeRegistryEntry<T>> createDeferredRegister(registry: IForgeRegistry<T>, owner: String): DeferredRegister<T> = object : DeferredRegister<T> {
                override val registry: IForgeRegistry<T> = registry
                override val owner: String = owner

                override fun <U : T> register(name: String, objectSupplier: () -> U): RegistryObject<U> = object : RegistryObject<U> {
                    override val name: NameSpacedString = NameSpacedString(name)
                    override val value: U? = null
                }

                override fun subscribeOnto(bus: EventBus) = Unit
            }

            override fun <T : IForgeRegistryEntry<T>, U : T> createRegistryObject(name: NameSpacedString, registry: IForgeRegistry<T>): RegistryObject<U> =
                    object: RegistryObject<U> {
                        override val name: NameSpacedString = name
                        override val value: U? = null
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

    val tagRegistry: TagRegistry
    fun <T : Any> createTagType(type: KClass<out T>, directoryName: String, toElement: (NameSpacedString) -> T, equalityEvaluator: (T, T) -> Boolean): TagType<T>
    fun <T : Any> createTag(tagType: TagType<T>, name: NameSpacedString, vararg initialElements: T): Tag<T>
    fun <T : Any> findTagType(name: String): TagType<T>?

    val compatibilityProviderRegistry: CompatibilityProviderRegistry

    val messageHandlerRegistry: MessageHandlerRegistry
    fun dispatchMessageTo(receiver: String, message: Message<*>)

    fun getDatabasePathFor(owner: String): Path
}

@ApiStatus.Experimental
interface ExperimentalBosonApi {
    fun <T : IForgeRegistryEntry<T>> createDeferredRegister(registry: IForgeRegistry<T>, owner: String): DeferredRegister<T>
    fun <T : IForgeRegistryEntry<T>, U : T> createRegistryObject(name: NameSpacedString, registry: IForgeRegistry<T>): RegistryObject<U>
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
