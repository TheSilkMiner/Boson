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

class DataPackLikeModContainerLocator(private val targetDirectory: String, private val kind: Kind = Kind.DATA) : Locator {
    companion object {
        private val l = L(MOD_NAME, "DataPackLikeModContainerLocator")
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
        l.info("Attempting to load data from ModContainers for all ModContainers: currently looking in '${this.targetDirectory}' with kind ${this.kind}")
        Loader.instance().modList.asSequence()
                .map { this.findPossibleLocations(it) }
                .flatten()
                .filterNotNull()
                .toList()
    }

    override val locations: List<Lazy<Location>> get() = this.lazyLocations

    private fun findPossibleLocations(container: ModContainer): List<Lazy<Location>?> {
        val list = mutableListOf<Lazy<Location>?>()
        list += this.findLazyLocation(container, container.modId) // Always try to add the respective one

        val idList = mutableListOf<String>()

        val containerSource = container.source
        var fileSystem: FileSystem? = null
        try {
            val data = when {
                containerSource.isFile -> { // This is a JAR mod
                    FileSystems.newFileSystem(containerSource.toPath(), null).let {
                        fileSystem = it
                        it.getPath("/${this.kind.directoryName}")
                    }
                }
                containerSource.isDirectory -> { // We are in a development environment
                    containerSource.toPath().resolve(this.kind.directoryName)
                }
                !containerSource.exists() -> {
                    l.debug("Source '$containerSource' for mod container '$container' not found while looking for other packs: skipping")
                    null
                }
                else -> {
                    throw IllegalStateException("Source '$containerSource' for mod container '$container' is not a file nor a directory: this should be impossible")
                }
            } ?: pathThatDoesNotExist


            if (!Files.exists(data)) {
                l.debug("No directory found in mod container '$container' that matches the path '${this.kind.directoryName}': skipping it now")
            } else {
                l.info("Successfully found directory '${this.kind.directoryName}' for mod container '$container': looping now")

                Files.walk(data, 1).forEach {
                    val name = it.fileName?.toString()?.removeSuffix("/") // Removes trailing '/' in case we are iterating inside a ZIP file
                    if (Files.isDirectory(it) && it != data && name != null && name != container.modId) {
                        val otherContainer = this.findContainerFor(name)
                        val line = "Mod container '$container' supplies resources for '${this.kind.directoryName}/$name'"
                        if (otherContainer != null) {
                            l.debug("$line, which represents the known container '$otherContainer': looping on it later on")
                        } else {
                            l.warn("$line, which is NOT KNOWN: assuming it's fine and queueing for looping")
                        }
                        idList += name
                    }
                }
            }
        } catch (e: IOException) {
            l.bigWarn("""
                An error has occurred while attempting to identify the directory for the candidate $container
                The container will now be skipped. The exception and relevant stacktrace are the following:
                
                ${e.stackTrace.joinToString(separator = "\n") { it.toString() }}
            """.trimIndent())
        } finally {
            fileSystem?.close()
        }

        idList.forEach { list += this.findLazyLocation(container, it) }

        return list
    }

    private fun findContainerFor(id: String) = Loader.instance().modList.find { it.modId == id }

    private fun findLazyLocation(container: ModContainer, id: String): Lazy<Location>? {
        val jsonDirectory = "${this.kind.directoryName}/$id/${this.targetDirectory}"
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
                !source.exists() -> {
                    l.debug("Source '$source' for mod container '$container' doesn't exist, it probably is a Launch Plugin or a JAR mod: skipping")
                    null
                }
                else -> {
                    throw IllegalStateException("Source '$source' for mod container '$container' is not a file nor a directory: this should be impossible")
                }
            } ?: pathThatDoesNotExist


            if (!Files.exists(root)) {
                l.debug("No directory found in mod container '$container' that matches the path '$jsonDirectory': will be skipped later on")
            } else {
                l.info("Successfully found directory '$jsonDirectory' for mod container '$container' representing '${this.findContainerFor(id)}': adding it to the list")
            }

            lazy {
                BaseLocation(root, container.name, BaseContext().apply {
                    this[modIdContextKey] = id
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
