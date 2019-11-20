@file:JvmName("ModContainerLocatorBuilder")

package net.thesilkminer.mc.boson.prefab.loader.locator

import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ModContainer
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.loader.Location
import net.thesilkminer.mc.boson.api.loader.Locator
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.prefab.loader.context.BaseContext
import net.thesilkminer.mc.boson.prefab.loader.location.BaseLocation
import net.thesilkminer.mc.boson.prefab.loader.modIdContextKey
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Files

class OneForAllModContainerLocator(private val lookupContainer: ModContainer, private val targetDirectory: String, private val kind: Kind = Kind.DATA) : Locator {
    companion object {
        private val l = L(MOD_NAME, "ModContainerLocator")
    }

    enum class Kind(val directoryName: String) {
        ASSET("assets"),
        DATA("data")
    }

    private val lazyLocations: List<Lazy<Location>>
        get() {
            l.info("Attempting to load data from target ModContainer '${this.lookupContainer}': currently looking in '${this.targetDirectory}' with kind ${this.kind}")
            return Loader.instance().modList.asSequence().map { this.findLazyLocation(it) }.filterNotNull().toList()
        }

    override val locations: List<Lazy<Location>> get() = this.lazyLocations

    private fun findLazyLocation(container: ModContainer): Lazy<Location>? {
        val jsonDirectory = "${this.kind.directoryName}/${container.modId}/${this.targetDirectory}"
        val source = this.lookupContainer.source

        return try {
            val root = when {
                source.isFile -> { // This is a JAR mod
                    FileSystems.newFileSystem(source.toPath(), null).use {
                        it.getPath("/${jsonDirectory}")
                    }
                }
                source.isDirectory -> { // We are in a development environment
                    source.toPath().resolve(jsonDirectory)
                }
                else -> {
                    throw IllegalStateException("Source is not a file nor a directory: this is impossible")
                }
            }

            if (root == null || !Files.exists(root)) {
                l.debug("No directory found in target Mod Container that matches the path '$jsonDirectory': skipping")
                null
            } else {
                lazy { BaseLocation(root, container.name, BaseContext().apply {
                    this[modIdContextKey] = container.modId
                }) }
            }
        } catch (e: IOException) {
            l.bigWarn("""
                An error has occurred while attempting to identify the directory for the candidate $container
                The container will now be skipped. The exception and relevant stacktrace are the following:
                
                ${e.stackTrace.joinToString(separator = "\n") { it.toString() }}
            """.trimIndent())
            null
        }
    }
}
