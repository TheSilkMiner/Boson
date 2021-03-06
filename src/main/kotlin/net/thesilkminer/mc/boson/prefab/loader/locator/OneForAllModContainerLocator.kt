/*
 * Copyright (C) 2021  TheSilkMiner
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
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths

class OneForAllModContainerLocator(private val lookupContainer: ModContainer, private val targetDirectory: String, private val kind: Kind = Kind.DATA) : Locator {
    companion object {
        private val l = L(MOD_NAME, "OneForAllModContainerLocator")
        private val pathThatDoesNotExist by lazy {
            var path = Paths.get("path.that.does.not.exist.because.is.illegal.in.most.file.systems.and.operating.systems")
            while (Files.exists(path)) path = path.resolve("path.that.does.not.exist.because.is.illegal.in.most.file.systems.and.operating.systems")
            path
        }
    }

    enum class Kind(val directoryName: String) {
        ASSET("assets"),
        DATA("data")
    }

    private val systemsStack = mutableListOf<FileSystem?>()

    private val lazyLocations: List<Lazy<Location>> by lazy {
        l.info("Attempting to load data from target ModContainer '${this.lookupContainer}': currently looking in '${this.targetDirectory}' with kind ${this.kind}")
        Loader.instance().modList.asSequence().map { this.findLazyLocation(it) }.filterNotNull().toList()
    }

    override val locations: List<Lazy<Location>> get() = this.lazyLocations

    private fun findLazyLocation(container: ModContainer): Lazy<Location>? {
        val jsonDirectory = "${this.kind.directoryName}/${container.modId}/${this.targetDirectory}"
        val source = this.lookupContainer.source

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
                !source.exists() -> {
                    l.debug("Source '$source' for mod container '$container' doesn't exist, it probably is a Launch Plugin or a JAR mod: skipping")
                    null
                }
                else -> {
                    throw IllegalStateException("Source '$source' for mod container '$container' is not a file nor a directory: this should be impossible")
                }
            } ?: pathThatDoesNotExist


            if (root != pathThatDoesNotExist) {
                if (!Files.exists(root)) {
                    l.debug("No directory found in mod container '$container' that matches the path '$jsonDirectory': will be skipped later on")
                } else {
                    l.info("Successfully found directory '$jsonDirectory' for mod container '$container': adding it to the list")
                }
            }

            lazy {
                BaseLocation(root, container.name, BaseContext().apply {
                    this[modIdContextKey] = container.modId
                })
            }
        } catch (e: IOException) {
            l.bigWarn("""
                An error has occurred while attempting to identify the directory for the candidate $container
                The container will now be skipped. The exception and relevant stacktrace are the following:
                
                ${e.stackTrace.joinToString(separator = "\n") { it.toString() }}
            """.trimIndent())
            null
        } finally {
            this.systemsStack.add(fileSystem)
        }
    }

    override fun clean() = this.systemsStack.reversed().asSequence().filterNotNull().forEach { it.close() }
}
