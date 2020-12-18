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

internal class BosonLoader(builder: LoaderBuilder) : Loader {
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
        try {
            this@BosonLoader.doLoading()
        } catch (e: Exception) {
            throw LoaderException("An exception has occurred while attempting to load with loader '${this@BosonLoader.name}'", e)
        }
    }

    private fun doLoading() = try {
        this.l.info("Loading process has started")
        this.progressReporter?.beginVisit()

        this.l.debug("Creating global context for Loader")
        val globalContext = this.globalContextBuilder?.buildContext(null)

        this.progressReporter?.visitPhases(this.phases.count())
        this.phases.forEachIndexed { index, phase ->
            this.l.info("Beginning phase $index: ${phase.name}")
            try {
                phase.attemptPhase(globalContext)
            } catch (e: Exception) {
                throw PhaseException("Unable to reach end of phase $index '${phase.name}' cleanly due to an error", e)
            }
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
        throw PhaseException("An error has occurred while executing phase '${this.name}'", e)
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
            this.exists() -> this.processFile(this, phase, globalContext, phaseContext)
        }
    }

    private fun Location.processDirectory(phase: LoadingPhase<Any>, globalContext: Context?, phaseContext: Context?) {
        Files.walk(this.path).use { file ->
            file.forEach { it.toLocation(this.additionalContext).processFile(this.path.relativize(it).toLocation(this.additionalContext), phase, globalContext, phaseContext) }
        }
    }

    private fun Location.processFile(relative: Location, phase: LoadingPhase<Any>, globalContext: Context?, phaseContext: Context?) {
        val name = this@BosonLoader.identifierBuilder.makeIdentifier(relative, globalContext, phaseContext)
        if (this.isFiltered(phase)) {
            return
        }
        try {
            this@BosonLoader.l.debug("Processing file '$name'")
            this.process(phase, name, globalContext, phaseContext)
        } catch (e: Exception) {
            throw ProcessingException("An error has occurred while attempting to process location '$this' (name is '$name')", e)
        }
    }

    private fun Location.process(phase: LoadingPhase<Any>, name: NameSpacedString, globalContext: Context?, phaseContext: Context?) {
        this@BosonLoader.progressReporter?.visitItem(name)
        val content = Files.newBufferedReader(this.path).use { file ->
            file.lines().asSequence().joinToString(separator = "\n") { it }
        }
        val pre = phase.preprocessor
        if (pre == null) {
            phase.processor.process(content, name, globalContext, phaseContext)
        } else {
            val preContent = pre.preProcessData(content, name, globalContext, phaseContext)
                    ?: return this@BosonLoader.l.debug("Pre-processor disabled file loading for '$name': aborting")
            phase.processor.process(preContent, name, globalContext, phaseContext)
        }
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
    override fun toString() = "${super.toString()}{wrapping '${this.path}'}"
}

private class LoaderException(message: String, cause: Throwable) : Exception(message, cause)
private class PhaseException(message: String, cause: Throwable) : Exception(message, cause)
private class ProcessingException(message: String, cause: Throwable) : Exception(message, cause)
