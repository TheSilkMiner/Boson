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

@file:JvmName("LoaderFactory")

package net.thesilkminer.mc.boson.api.loader

import net.thesilkminer.mc.boson.api.bosonApi
import kotlin.collections.ArrayList

@DslMarker
annotation class LoaderDsl

@LoaderDsl
class LoaderBuilder {
    lateinit var identifierBuilder: IdentifierBuilder

    var globalContextBuilder: ContextBuilder? = null
    var name: String? = null
    var progressVisitor: ProgressVisitor? = null

    private val locators = mutableListOf<Locator>()
    private val phases = mutableListOf<LoaderPhaseBuilder>()

    fun locators(block: LoaderLocators.() -> Unit) {
        this.locators.addAll(LoaderLocators().apply(block))
    }

    fun phases(block: LoaderPhases.() -> Unit) {
        this.phases.addAll(LoaderPhases().apply(block))
    }

    fun locators() = listOf(*this.locators.toTypedArray())
    fun phases() = listOf(*this.phases.toTypedArray())

    fun build(): Loader = bosonApi.buildLoader(this)
}

@LoaderDsl
class LoaderLocators : ArrayList<Locator>() {
    fun locator(locatorSupplier: () -> Locator) {
        this.add(locatorSupplier())
    }
}

@LoaderDsl
class LoaderPhases : ArrayList<LoaderPhaseBuilder>() {
    operator fun String.invoke(block: LoaderPhaseBuilder.() -> Unit) {
        this@LoaderPhases.add(LoaderPhaseBuilder(this).apply(block))
    }
}

@LoaderDsl
class LoaderPhaseBuilder(val name: String) {
    var contextBuilder: ContextBuilder? = null
    var preprocessor: Preprocessor<String, *>? = null
    lateinit var processor: Processor<*>

    private val filters = mutableListOf<Filter>()

    fun filters(block: LoaderPhaseFilters.() -> Unit) {
        this.filters.addAll(LoaderPhaseFilters().apply(block))
    }

    fun filters() = listOf(*this.filters.toTypedArray())
}

@LoaderDsl
class LoaderPhaseFilters : ArrayList<Filter>() {
    fun filter(filterSupplier: () -> Filter) {
        this.add(filterSupplier())
    }
}

fun loader(block: LoaderBuilder.() -> Unit) = LoaderBuilder().apply(block).build()
