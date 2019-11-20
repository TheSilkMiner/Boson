package net.thesilkminer.mc.boson.implementation.loader

import net.thesilkminer.kotlin.commons.lang.rethrowAs
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.Filter
import net.thesilkminer.mc.boson.api.loader.Loader
import net.thesilkminer.mc.boson.api.loader.LoaderBuilder
import net.thesilkminer.mc.boson.api.loader.LoaderPhaseBuilder
import net.thesilkminer.mc.boson.api.loader.LoadingPhase
import net.thesilkminer.mc.boson.api.loader.Location
import net.thesilkminer.mc.boson.api.loader.Locator
import net.thesilkminer.mc.boson.api.loader.Preprocessor
import net.thesilkminer.mc.boson.api.loader.Processor
import net.thesilkminer.mc.boson.api.log.L
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence

class BosonLoader(builder: LoaderBuilder) : Loader {
    private val l by lazy { L("Boson Loader", this.name) }

    private val name = builder.name ?: this.hashCode().toString()
    private val locators = builder.locators().apply { check(this.isNotEmpty()) { "You must specify at least one locator for the loader" } }
    private val globalContextBuilder = builder.globalContextBuilder
    private val identifierBuilder = builder.identifierBuilder
    private val progressReporter = builder.progressVisitor
    private val phases = builder.phases().map { BosonLoadingPhase<Any>(it) }.apply { check(this.isNotEmpty()) { "Unable to create a loader without phases" } }

    init {
        this.l.info("Loader initialized, waiting for requests")
    }

    override fun load() = with(this.l) {
        this.info("Using Loader ${this.name}, with ${this@BosonLoader.locators.count()} locators and ${this@BosonLoader.phases.count()} phases")
        this.debug("Locators: ${this@BosonLoader.locators}")
        this.debug("Identifier builder: ${this@BosonLoader.identifierBuilder}")
        this.debug("Progress Visitor: ${this@BosonLoader.progressReporter}")
        this@BosonLoader.doLoading()
    }

    private fun doLoading() = try {
        this.l.info("Loading process has started")
        this.progressReporter?.beginVisit()

        this.l.debug("Creating global context for Loader")
        val globalContext = this.globalContextBuilder?.buildContext(null)

        this.progressReporter?.visitPhases(this.phases.count())
        this.phases.forEachIndexed { index, phase ->
            this.l.info("Beginning phase $index: ${phase.name}")
            phase.attemptPhase(globalContext)
            this.l.info("Reached end of phase ${phase.name} successfully")
        }

        this.l.info("Cleaning up loader resources")
        this.locators.forEach { it.clean() }

        this.l.info("Loading process completed")
        this.progressReporter?.endVisit() ?: Unit // Okay, what?
    } catch (e: Exception) {
        e rethrowAs ::LoaderException
    }

    private fun LoadingPhase<Any>.attemptPhase(globalContext: Context?) = try {
        this.goThroughPhase(globalContext)
    } catch (e: Exception) {
        throw PhaseException("An error has occurred while executing phase ${this.name}", e)
    }

    private fun LoadingPhase<Any>.goThroughPhase(globalContext: Context?) {
        this@BosonLoader.progressReporter?.visitPhase(this)

        val locators = this@BosonLoader.locators
        val itemsToLoad = locators.flatMap { it.locations }
        this@BosonLoader.l.debug("Attempting to load a total of ${itemsToLoad.count()} items")
        this@BosonLoader.progressReporter?.visitItemsTotal(itemsToLoad.count())

        val phaseContext = this.contextBuilder?.buildContext(this)

        locators.forEach { it.loadThroughLocator(this, globalContext, phaseContext) }
    }

    private fun Locator.loadThroughLocator(phase: LoadingPhase<Any>, globalContext: Context?, phaseContext: Context?) {
        this@BosonLoader.l.debug("Attempting to load data through locator $this")
        val locations = this.locations
        this@BosonLoader.progressReporter?.visitItems(locations.count())
        locations.forEach { it.value.processLocation(phase, globalContext, phaseContext) }
    }

    private fun Location.processLocation(phase: LoadingPhase<Any>, globalContext: Context?, phaseContext: Context?) {
        this@BosonLoader.progressReporter?.visitLocation(this, this.isDirectory())
        when {
            this.isDirectory() -> this.processDirectory(phase, globalContext, phaseContext)
            this.exists() -> this.processFile(phase, globalContext, phaseContext)
            else -> this@BosonLoader.l.debug("Skipping location '$this' because it does not exist: please complain to your nearest cat")
        }
    }

    private fun Location.processDirectory(phase: LoadingPhase<Any>, globalContext: Context?, phaseContext: Context?) {
        this@BosonLoader.l.info("Attempting to read all files inside the directory '$this'")
        Files.walk(this.path, 1).forEach { it.relativize(this.path).toLocation(this.additionalContext).processFile(phase, globalContext, phaseContext) }
    }

    private fun Location.processFile(phase: LoadingPhase<Any>, globalContext: Context?, phaseContext: Context?) {
        this@BosonLoader.l.debug("Attempting to read file '$this'")
        val name = this@BosonLoader.identifierBuilder.makeIdentifier(this, globalContext, phaseContext)
        if (this.isFiltered(phase)) {
            this@BosonLoader.l.info("Skipping processing of file '$name' because it was filtered")
            return
        }
        this.process(phase, name, globalContext, phaseContext)
    }

    private fun Location.process(phase: LoadingPhase<Any>, name: NameSpacedString, globalContext: Context?, phaseContext: Context?) {
        this@BosonLoader.progressReporter?.visitItem(name)
        this@BosonLoader.l.debug("Reading data from '$name'")
        val content = Files.newBufferedReader(this.path).lines().asSequence().joinToString(separator = "\n") { it }
        phase.processor.process(phase.preprocessor?.preProcessData(content, name, globalContext, phaseContext) ?: content, name, globalContext, phaseContext)
    }

    private fun Location.isFiltered(phase: LoadingPhase<*>) = phase.filters.any { !it.canLoad(this) }
    private fun Location.exists() = this.path.exists()
    private fun Location.isDirectory() = this.path.isDirectory()
    private fun Path.exists() = Files.exists(this)
    private fun Path.isDirectory() = Files.isDirectory(this)
    private fun Path.toLocation(context: Context?) = LocationPathWrapper(this, context)
}

private class BosonLoadingPhase<T : Any>(builder: LoaderPhaseBuilder) : LoadingPhase<T> {
    override val name = builder.name
    override val filters: List<Filter> = builder.filters()
    override val contextBuilder = builder.contextBuilder
    override val preprocessor = builder.preprocessor?.uncheckedCast<Preprocessor<String, T>>()
    override val processor = builder.processor.uncheckedCast<Processor<T>>()
}

private class LocationPathWrapper(override val path: Path, override val additionalContext: Context?) : Location {
    override val friendlyName: String? = null
}

private class LoaderException(message: String, cause: Throwable) : Exception(message, cause)
private class PhaseException(message: String, cause: Throwable) : Exception(message, cause)
