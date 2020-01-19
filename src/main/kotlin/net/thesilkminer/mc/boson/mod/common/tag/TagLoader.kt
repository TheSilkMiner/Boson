@file:JvmName("TL")

package net.thesilkminer.mc.boson.mod.common.tag

import com.google.common.base.CaseFormat
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.oredict.OreDictionary
import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.ContextKey
import net.thesilkminer.mc.boson.api.loader.Filter
import net.thesilkminer.mc.boson.api.loader.Location
import net.thesilkminer.mc.boson.api.loader.Locator
import net.thesilkminer.mc.boson.api.loader.Processor
import net.thesilkminer.mc.boson.api.loader.loader
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.api.modid.FORGE
import net.thesilkminer.mc.boson.api.tag.TagType
import net.thesilkminer.mc.boson.prefab.loader.context.BaseContext
import net.thesilkminer.mc.boson.prefab.loader.context.BaseContextBuilder
import net.thesilkminer.mc.boson.prefab.loader.filter.JsonFileFilter
import net.thesilkminer.mc.boson.prefab.loader.filter.RegularFileFilter
import net.thesilkminer.mc.boson.prefab.loader.filter.SpecialFileFilter
import net.thesilkminer.mc.boson.prefab.loader.location.BaseLocation
import net.thesilkminer.mc.boson.prefab.loader.locator.DataPackLikeModContainerLocator
import net.thesilkminer.mc.boson.prefab.loader.locator.ResourcesDirectoryLocator
import net.thesilkminer.mc.boson.prefab.loader.modIdContextKey
import net.thesilkminer.mc.boson.prefab.loader.naming.DefaultIdentifierBuilder
import net.thesilkminer.mc.boson.prefab.loader.preprocessor.CatchingPreprocessor
import net.thesilkminer.mc.boson.prefab.loader.preprocessor.JsonConverterPreprocessor
import net.thesilkminer.mc.boson.prefab.loader.processor.CatchingProcessor
import net.thesilkminer.mc.boson.prefab.loader.progress.ActiveModContainerVisitor
import net.thesilkminer.mc.boson.prefab.loader.progress.ProgressBarVisitor
import java.io.IOException
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files

private val l = L(MOD_NAME, "Data-Pack Loader")
private val markerContextKey = ContextKey("is_adapter", Boolean::class)

private val tagLoader = loader {
    name = "Tag Loader"
    progressVisitor = ProgressBarVisitor().chain(ActiveModContainerVisitor())
    globalContextBuilder = BaseContextBuilder()
    identifierBuilder = DefaultIdentifierBuilder(removeExtension = true)
    locators {
        locator {
            object : Locator {
                private val l = L(MOD_NAME, this::class.java.name)
                private val systemsStack = mutableListOf<FileSystem?>()
                private val lazyLocations: List<Lazy<Location>> by lazy {
                    l.info("Attempting to load Minecraft tags from Boson: looking inside 'tags' for 'DATA'")
                    Loader.instance().modList.asSequence()
                            .filter { it.modId == MOD_ID }
                            .map { listOf(this.findLazyLocation(it, "minecraft"), this.findLazyLocation(it, "forge")) }
                            .flatten()
                            .toList()
                }

                override val locations: List<Lazy<Location>> get() = this.lazyLocations

                private fun findLazyLocation(container: ModContainer, id: String): Lazy<Location> {
                    val jsonDirectory = "data/$id/tags"
                    val source = container.source

                    var fileSystem: FileSystem? = null
                    return try {
                        val root = when {
                            source.isFile -> { // This is a JAR mod
                                FileSystems.newFileSystem(source.toPath(), null).let {
                                    fileSystem = it
                                    it.getPath("/${jsonDirectory}")
                                }
                            }
                            source.isDirectory -> { // We are in a development environment
                                source.toPath().resolve(jsonDirectory)
                            }
                            else -> {
                                null
                            }
                        } ?: throw IllegalStateException("Unable to find path inside Boson mod container: this is a serious issue, and it should be impossible!")


                        if (!Files.exists(root)) {
                            throw IllegalStateException("Unable to find path inside Boson mod container: this is a serious issue, and it should be impossible!")
                        } else {
                            l.info("Successfully found the directory '$jsonDirectory' inside mod container '$container': adding it to the list")
                        }

                        lazy {
                            BaseLocation(root, "Minecraft tags - $id", BaseContext().apply {
                                this[modIdContextKey] = id
                                this[markerContextKey] = true
                            })
                        }
                    } catch (e: IOException) {
                        l.bigWarn("""
                            An error has occurred while attempting to identify the MC directory inside the container $container
                            This should never ever happen! The game will now crash
                        """.trimIndent())
                        throw IllegalStateException("Unable to find path inside Boson mod container: this is a serious issue", e)
                    } finally {
                        this.systemsStack.add(fileSystem)
                    }
                }

                override fun clean() = this.systemsStack.reversed().asSequence().filterNotNull().forEach { it.close() }
            }
        }
        locator {
            object : Locator {
                private val l = L(MOD_NAME, this::class.java.name)
                private val deferredLocator = DataPackLikeModContainerLocator(targetDirectory = "tags")
                private val bosonModContainerName = Loader.instance().modList.find { it.modId == MOD_ID }!!.name

                private val lazyLocations: List<Lazy<Location>> by lazy {
                    this.deferredLocator.locations
                            .asSequence()
                            .filter {
                                val location = it.value
                                val context = location.additionalContext ?: return@filter true
                                val modIdKey = context[modIdContextKey] ?: return@filter true
                                (location.friendlyName != this.bosonModContainerName || (modIdKey != "minecraft" && modIdKey != "forge")).apply {
                                    if (!this) l.info("Filtered location '${location.path}' because it was already explored with a previous Locator")
                                }
                            }
                            .toList()
                }

                override val locations: List<Lazy<Location>> get() = this.lazyLocations
            }
        }
        locator { ResourcesDirectoryLocator(targetDirectory = "tags") }
    }
    phases {
        "Ensuring schema cleanliness" {
            filters {
                filter { SpecialFileFilter(kind = SpecialFileFilter.Kind.JSON_SCHEMA) }
            }
            processor = object : Processor<Any> {
                @Suppress("GrazieInspection")
                override fun process(content: Any, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?) {
                    throw IllegalStateException("File name 'pattern.json' is invalid.\nThat name is reserved in JSON and has a special meaning " +
                            "that does not apply to this case.\nPlease remove or rename the invalid file.\nID of the broken entry: $identifier" )
                }
            }
        }
        "Loading Minecraft tags" {
            filters {
                filter { RegularFileFilter() }
                filter { JsonFileFilter() }
                filter {
                    object : Filter {
                        override fun canLoad(location: Location) = location.additionalContext?.ifPresent(markerContextKey) { it } ?: false
                    }
                }
            }
            preprocessor = CatchingPreprocessor(logger = l, preprocessor = JsonConverterPreprocessor())
            processor = CatchingProcessor(logger = l, processor = TagLoadingProcessor(true))
        }
        "Checking JSON tag structure" {
            filters {
                filter { SpecialFileFilter(kind = SpecialFileFilter.Kind.UNDERSCORE_PREFIX) }
            }
            processor = object : Processor<Any> {
                override fun process(content: Any, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?) {
                    l.warn("Found entry beginning with '_' inside tags namespace: this will not be processed. Entry ID: $identifier")
                }
            }
        }
        "Loading tags" {
            filters {
                filter { RegularFileFilter() }
                filter { JsonFileFilter() }
                filter { SpecialFileFilter(kind = SpecialFileFilter.Kind.UNDERSCORE_PREFIX, inverted = true) }
                filter {
                    object : Filter {
                        override fun canLoad(location: Location) = !(location.additionalContext?.ifPresent(markerContextKey) { it } ?: false)
                    }
                }
            }
            preprocessor = CatchingPreprocessor(logger = l, preprocessor = JsonConverterPreprocessor())
            processor = CatchingProcessor(logger = l, processor = TagLoadingProcessor(false))
        }
    }
}

fun loadTags() {
    l.info("Attempting to load data-pack tags from all mod locators")
    tagLoader.load()
    l.info("Loading has completed")
}

fun initializeTagOreDictCompatibilityLayer() {
    l.info("Initializing ore dictionary compatibility layer: attempting to tag all items of the ore dictionary!")
    l.warn("The ore dictionary is deprecated and it WON'T survive a 1.13+ update: consider using tags")
    var compatElements = 0
    val tagType = TagType.find<ItemStack>("items")!!
    OreDictionary.getOreNames()
            .asSequence()
            .map { OreDictionary.getOres(it).toList().map { stack -> it!! to stack!! } }
            .flatten()
            .distinct()
            .forEach {
                val name = it.first.createTagName()
                l.debug("Registering '${it.second}' to tag named '$name' (type '${tagType.name}')")
                bosonApi.tagRegistry[tagType, name] += it.second
                ++compatElements
            }
    l.info("Compatibility layer successfully initialized: registered compatibility for $compatElements ore dictionary names")
}

@Suppress("SpellCheckingInspection")
private fun String.createTagName(): NameSpacedString {
    val snakeOre = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this)!!
    return NameSpacedString(FORGE, "oredict/$snakeOre")
}
