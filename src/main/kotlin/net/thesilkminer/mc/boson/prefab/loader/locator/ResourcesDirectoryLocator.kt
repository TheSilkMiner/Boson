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

package net.thesilkminer.mc.boson.prefab.loader.locator

import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.loader.Location
import net.thesilkminer.mc.boson.api.loader.Locator
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.prefab.loader.context.BaseContext
import net.thesilkminer.mc.boson.prefab.loader.location.BaseLocation
import net.thesilkminer.mc.boson.prefab.loader.modIdContextKey
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.asSequence

class ResourcesDirectoryLocator(private val targetDirectory: String, private val kind: Kind = Kind.DATA) : Locator {
    companion object {
        private val l = L(MOD_NAME, "ResourcesDirectoryLocator")
    }

    enum class Kind(val directoryName: String) {
        ASSET("assets"),
        DATA("data")
    }

    private val walkStack = mutableListOf<AutoCloseable>()

    private val lazyLocations : List<Lazy<Location>> by lazy {
        l.info("Attempting to load data from the 'resources/' directory, situated in your main game directory.")
        l.info("We are currently looking in the '${this.targetDirectory}' directory with kind ${this.kind}")
        this.scanResourcesDirectory(Paths.get(".").resolve("./resources").normalize().toAbsolutePath())
    }

    override val locations: List<Lazy<Location>> get() = this.lazyLocations

    private fun scanResourcesDirectory(resources: Path) =
            this.scanAllDirectories(resources.resolve("./${this.kind.directoryName}/").normalize().toAbsolutePath(), resources)
    private fun scanAllDirectories(data: Path, resources: Path) = if (Files.exists(data)) {
        Files.walk(data, 1)
                .apply { this@ResourcesDirectoryLocator.walkStack += this }
                .asSequence()
                .filter { !it.parent.fileName.toString().endsWith("resources") }
                .flatMap { this.scanDirectory(it, resources) }
                .toList()
    } else {
        l.info("Directory '$data' doesn't exist: skipping resources loading")
        listOf()
    }
    private fun scanDirectory(id: Path, resources: Path) =
            this.scanForFiles(id.resolve("./${this.targetDirectory}").normalize().toAbsolutePath(), id, resources)
    private fun scanForFiles(target: Path, id: Path, resources: Path) = this.let {
        val modId = id.fileName.toString()
        if (!Files.exists(target)) {
            l.warn("Directory '${resources.relativize(target)}' doesn't exist: skipping loading of files")
        } else {
            l.info("Successfully identified directory '${resources.relativize(target)}': proceeding with loading of files")
        }
        sequenceOf(lazy {
            BaseLocation(target.toAbsolutePath(), "$modId - User-Added Resources", BaseContext().apply {
                this[modIdContextKey] = modId
            })
        })
    }

    override fun clean() = this.walkStack.reversed().forEach { it.close() }
}
