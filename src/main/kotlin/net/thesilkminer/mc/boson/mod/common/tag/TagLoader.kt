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
                private val l = L(MOD_NAME, this::class.java.name.shorten())
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
                            BaseLocation(root, "Minecraft tags - ${if (id == "minecraft") "Minecraft" else "Minecraft Forge"}", BaseContext().apply {
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
                private val l = L(MOD_NAME, this::class.java.name.shorten())
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

private fun String.shorten() = this.substring(startIndex = this.lastIndexOf('.') + 1, endIndex = this.length)

internal fun loadTags() {
    l.info("Attempting to load data-pack tags from all mod locators")
    tagLoader.load()
    l.info("Loading has completed")
}

internal fun initializeTagOreDictCompatibilityLayer() {
    l.info("Initializing ore dictionary compatibility layer: attempting to tag all items of the ore dictionary!")
    setUpBasicOreDictToTagCompatibility()
    attemptNormalizationOfOreDictNamesToTags()
    // Should I also provide tag -> ore dictionary compatibility (i.e. forge:ingots/copper -> ingotCopper)?
    l.info("Compatibility layer successfully initialized fully: ore dictionary -> tag compatibility is now in place")
}

private fun setUpBasicOreDictToTagCompatibility() {
    l.info("Setting up basic ore dictionary -> tag compatibility layer")
    var compatElements = 0
    val tagType = net.thesilkminer.mc.boson.prefab.tag.itemTagType
    getOreDictionaryPairs().forEach {
        val name = it.first.createTagName()
        l.debug("Registering '${it.second}' to tag named '$name' (type '${tagType.name}')")
        bosonApi.tagRegistry[tagType, name] += it.second
        ++compatElements
    }
    l.info("Ore Dictionary -> Tag compatibility layer successfully initialized: registered compatibility for $compatElements ore dictionary names")
    if (compatElements > 0) l.warn("The ore dictionary is deprecated and WON'T survive a 1.13+ update: consider using tags")
}

private fun attemptNormalizationOfOreDictNamesToTags() {
    l.info("Attempting to provide special handling for certain ore dictionary entries into tags")

    // The main idea is to have things such as oreCopper be registered under forge:ores/copper and forge:ores
    // much like ingotEnder be under forge:ingots/ender and forge:ingots
    // This way there's no need to have to specify forge:oredict/... tags anymore unless the entry cannot be translated
    // automatically into a tag. This is still work in progress.

    val tagType = net.thesilkminer.mc.boson.prefab.tag.itemTagType
    val listAllTags = mutableListOf<Pair<String, ItemStack>>()

    getOreDictionaryPairs()
            .filter { it.first.isValidOreDictionaryName().also { r -> if (!r) l.debug("No special handler registered for name '${it.first}'") } } // Filter ore dictionary names with handlers
            .onEach { if (it.first.startsWith("listAll")) listAllTags += it } // Separate all listAllXxx so that we can add them back at the end
            .filterNot { it.first.startsWith("listAll") }
            .sortedBy { it.first }
            .plus(listAllTags.toList().sortedBy { it.first }.also { listAllTags.clear() })
            .map { it.first.findTagNameSpacedStrings().map { name -> name.also { _ -> l.debug("Mapping ore dictionary '${it.first}' to tag '$name'") } to it.second } }
            .flatten() // Now the sequence is a sequence of Pairs where first == name of the tag where the item should be added and second == the item to add
            .onEach { bosonApi.tagRegistry[tagType, it.first] += it.second }
            .map { Pair(it.first.findMatchingParentTagName(), it.first) } // Now first is the list of parents for the original tag and second is the original tag
            .filterNot { it.first.isEmpty() } // We remove all tags whose parents are empty
            .map { it.first.map { parent -> Pair(parent, it.second) } }
            .flatten() // And now the sequence is a sequence of pairs where it.first is one of the parents of it.second
            .distinct()
            .forEach { bosonApi.tagRegistry[tagType, it.first] += bosonApi.tagRegistry[tagType, it.second] } // Here we add the tag identified by second to the one identified by first
}

private fun getOreDictionaryPairs() = OreDictionary.getOreNames()
        .asSequence()
        .map { OreDictionary.getOres(it).toList().map { stack -> it!! to stack!! } }
        .flatten()
        .distinct()

@Suppress("SpellCheckingInspection")
private fun String.createTagName(): NameSpacedString {
    return fnss("oredict/${this.toLowerUnderscore(lower = true)}")
}

private val validPrefixes = mutableMapOf<String, (String) -> List<NameSpacedString>>().apply { this.addAllValidPrefixes() }.toMap()
private val filteredForParents = listOf<String>()
private val specialParentHandling = mutableListOf<Pair<(NameSpacedString) -> Boolean, NameSpacedString>>().apply { this.addAllSpecialParents() }.toList()

private fun String.isValidOreDictionaryName(): Boolean = validPrefixes.keys.any { this.startsWith(it) }

private fun String.findTagNameSpacedStrings(): List<NameSpacedString> = validPrefixes.asSequence()
        .filter { this.startsWith(it.key) }
        .map { it.value(this.removePrefix(it.key)) }
        .flatten()
        .toList()

private fun NameSpacedString.findMatchingParentTagName(): List<NameSpacedString> {
    val parents = mutableListOf<NameSpacedString>()
    parents += this.trySpecialHandling()
    if (!this.path.contains('/')) return parents
    val pathBeginning = this.path.substringBefore('/')
    if (pathBeginning.isOreDictionaryFiltered()) return parents
    parents += NameSpacedString(this.nameSpace, pathBeginning)
    return parents
}

private fun NameSpacedString.trySpecialHandling(): List<NameSpacedString> = specialParentHandling.asSequence().filter { it.first(this) }.map { it.second }.toList()

private fun String.isOreDictionaryFiltered(): Boolean = this in filteredForParents

private fun MutableMap<String, (String) -> List<NameSpacedString>>.addAllValidPrefixes() {
    fun glass(it: String, s: String = ""): List<NameSpacedString> = when {
        it.isEmpty() -> listOf()
        it == "Colorless" -> lnss("glass$s/colorless")
        else -> listOf(fnss("glass$s/${it.toLowerUnderscore()}"), fnss("stained_glass$s"))
    }

    fun chest(it: String): List<NameSpacedString> = when {
        it.isEmpty() -> listOf()
        it.startsWith("Wood") -> lnss("chests/wooden")
        it.startsWith("Ender") -> lnss("chests/ender")
        it.startsWith("Trapped") -> lnss("chests/trapped")
        else -> lnss("chests")
    }

    fun list(it: String): List<NameSpacedString> = when {
        it.isEmpty() -> listOf()
        it.startsWith("Food", ignoreCase = true) -> lnss("food")
        else -> listOf<NameSpacedString>().also { _ -> l.warn("listAll$it is not yet supported") }
    }

    val nonStorageBlocks = listOf("Cactus", "Slime", "Prismarine", "Glass")

    this["log"] = { lnss("logs") }
    this["plank"] = { lnss("planks") }
    this["slab"] = { if (it.startsWith("Wood")) listOf() else lnss("slabs/${it.toLowerUnderscore()}") }
    this["slabWood"] = { lnss("slabs/wooden") }
    this["fence"] = { if (it.startsWith("Wood") || it.startsWith("Gate")) listOf() else lnss("fences/${it.toLowerUnderscore()}") }
    this["fenceWood"] = { lnss("fences/wooden") }
    this["fenceGate"] = { if (it.startsWith("Wood")) listOf() else lnss("fence_gates/${it.toLowerUnderscore()}") }
    this["fenceGateWood"] = { lnss("fence_gates/wooden") }
    this["door"] = { if (it.startsWith("Wood")) listOf() else lnss("doors/${it.toLowerUnderscore()}") }
    this["doorWood"] = { lnss("doors/wooden") }
    this["stickWood"] = { lnss("rods/wooden") }
    this["treeSapling"] = { lnss("saplings") }
    this["treeLeaves"] = { lnss("leaves") }
    this["ore"] = { lnss("ores/${it.toLowerUnderscore()}") }
    this["ingot"] = { lnss("ingots/${it.toLowerUnderscore()}") }
    this["nugget"] = { lnss("nuggets/${it.toLowerUnderscore()}") }
    this["gem"] = { lnss("gems/${it.toLowerUnderscore()}") }
    this["dust"] = { lnss("dusts/${it.toLowerUnderscore()}") }
    this["block"] = { if (nonStorageBlocks.any { block -> it.startsWith(block) }) listOf() else lnss("storage_blocks/${it.toLowerUnderscore()}") }
    this["crop"] = { lnss("crops/${it.toLowerUnderscore()}") }
    this["dye"] = { if (it.isEmpty()) lnss("dyes") else lnss("dyes/${it.toLowerUnderscore()}") }
    this["slimeball"] = { lnss("slimeballs") }
    this["enderpearl"] = { lnss("ender_pearls") }
    this["bone"] = { lnss("bones") }
    this["gunpowder"] = { lnss("gunpowder") }
    this["string"] = { lnss("strings") }
    this["netherStar"] = { lnss("nether_stars") }
    this["leather"] = { lnss("leather") }
    this["feather"] = { lnss("feathers") }
    this["egg"] = { lnss("eggs") }
    this["record"] = { lnss("music_discs") }
    this["stone"] = { lnss("stone") }
    this["cobblestone"] = { lnss("cobblestone") }
    this["gravel"] = { lnss("gravel") }
    this["sand"] = { lnss("sand") }
    this["sandstone"] = { lnss("sandstone") }
    this["netherrack"] = { lnss("netherrack") }
    this["obsidian"] = { lnss("obsidian") }
    this["endstone"] = { lnss("end_stones") }
    this["blockGlass"] = { glass(it) }
    this["paneGlass"] = { glass(it, "_panes") }
    this["chest"] = { chest(it) }
    this["listAll"] = { list(it) }
}

private fun MutableList<Pair<(NameSpacedString) -> Boolean, NameSpacedString>>.addAllSpecialParents() {
    fun make(parent: NameSpacedString, predicate: (NameSpacedString) -> Boolean) = Pair(predicate, parent)

    this += make(mnss("logs"), forgeTag("logs/"))
    this += make(mnss("planks"), forgeTag("planks/"))
    this += make(mnss("slabs")) { forgeTag("slabs/")(it) && it.path != "slabs/wooden" }
    this += make(mnss("wooden_slabs"), forgeTag("slabs/wooden"))
    this += make(mnss("fences")) { forgeTag("fences/")(it) && it.path != "fences/wooden" }
    this += make(mnss("wooden_fences"), forgeTag("fences/wooden"))
    this += make(mnss("doors")) { forgeTag("doors/")(it) && it.path != "doors/wooden" }
    this += make(mnss("wooden_doors"), forgeTag("doors/wooden"))
    this += make(mnss("saplings"), forgeTag("saplings"))
    this += make(mnss("leaves"), forgeTag("leaves"))
    this += make(mnss("music_discs"), forgeTag("music_discs"))
    this += make(mnss("sand"), forgeTag("sand"))
}

private fun String.toLowerUnderscore(lower: Boolean = false) = (if (lower) CaseFormat.LOWER_CAMEL else CaseFormat.UPPER_CAMEL).to(CaseFormat.LOWER_UNDERSCORE, this)!!

@Suppress("SpellCheckingInspection") private fun lnss(path: String) = listOf(fnss(path))
@Suppress("SpellCheckingInspection") private fun mnss(path: String) = NameSpacedString(path)
@Suppress("SpellCheckingInspection") private fun fnss(path: String) = NameSpacedString(FORGE, path)

private fun forgeTag(prefix: String) = { it: NameSpacedString -> it.nameSpace == FORGE && it.path.startsWith(prefix) }
