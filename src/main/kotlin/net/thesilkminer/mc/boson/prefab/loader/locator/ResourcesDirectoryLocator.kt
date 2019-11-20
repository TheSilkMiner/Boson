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

    private val lazyLocations : List<Lazy<Location>>
        get() {
            l.info("Attempting to load data from the 'resources/' directory, situated in your main game directory.")
            l.info("We are currently looking in the '${this.targetDirectory}' directory with kind ${this.kind}")
            return this.scanResourcesDirectory(Paths.get(".").resolve("./resources").normalize().toAbsolutePath())
        }

    override val locations: List<Lazy<Location>> get() = this.lazyLocations

    private fun scanResourcesDirectory(resources: Path) =
            this.scanAllDirectories(resources.resolve("./${this.kind.directoryName}/").normalize().toAbsolutePath(), resources)
    private fun scanAllDirectories(data: Path, resources: Path) =
            Files.walk(data, 1).asSequence().flatMap { this.scanDirectory(it, resources) }.toList()
    private fun scanDirectory(id: Path, resources: Path) =
            this.scanForFiles(id.resolve("./${this.targetDirectory}").normalize().toAbsolutePath(), id, resources)
    private fun scanForFiles(target: Path, id: Path, resources: Path): Sequence<Lazy<Location>> = if (!Files.exists(target)) {
        l.warn("Directory '${resources.relativize(target)}' doesn't exist: skipping loading of files")
        emptySequence()
    } else {
        l.info("Successfully identified directory '${resources.relativize(target)}': proceeding with loading of files")
        val modId = id.fileName.toString()
        sequenceOf(lazy { BaseLocation(target.toAbsolutePath(), "$modId - User-Added Resources", BaseContext().apply {
            this[modIdContextKey] = modId
        }) })
    }
}
