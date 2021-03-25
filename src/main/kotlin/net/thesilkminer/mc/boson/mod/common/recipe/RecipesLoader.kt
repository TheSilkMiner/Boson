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

@file:JvmName("RL")

package net.thesilkminer.mc.boson.mod.common.recipe

import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.Filter
import net.thesilkminer.mc.boson.api.loader.Location
import net.thesilkminer.mc.boson.api.loader.Processor
import net.thesilkminer.mc.boson.api.loader.loader
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.mod.common.common
import net.thesilkminer.mc.boson.prefab.loader.context.BaseContextBuilder
import net.thesilkminer.mc.boson.prefab.loader.filter.JsonFileFilter
import net.thesilkminer.mc.boson.prefab.loader.filter.RegularFileFilter
import net.thesilkminer.mc.boson.prefab.loader.filter.SpecialFileFilter
import net.thesilkminer.mc.boson.prefab.loader.locator.ModContainerLocator
import net.thesilkminer.mc.boson.prefab.loader.locator.ResourcesDirectoryLocator
import net.thesilkminer.mc.boson.prefab.loader.naming.DefaultIdentifierBuilder
import net.thesilkminer.mc.boson.prefab.loader.preprocessor.CatchingPreprocessor
import net.thesilkminer.mc.boson.prefab.loader.preprocessor.JsonConverterPreprocessor
import net.thesilkminer.mc.boson.prefab.loader.processor.CatchingProcessor
import net.thesilkminer.mc.boson.prefab.loader.progress.ActiveModContainerVisitor
import net.thesilkminer.mc.boson.prefab.loader.progress.ProgressBarVisitor

private val l = L(MOD_NAME, "Data-Pack Loader")

private val recipesLoader = loader {
    name = "Data-Pack Recipes Loader"
    progressVisitor = ProgressBarVisitor().chain(ActiveModContainerVisitor())
    globalContextBuilder = BaseContextBuilder()
    identifierBuilder = DefaultIdentifierBuilder(removeExtension = true)
    locators {
        locator { ModContainerLocator(targetDirectory = "recipes") }
        locator { ResourcesDirectoryLocator(targetDirectory = "recipes") }
    }
    phases {
        "Ensuring schema cleanliness" {
            filters {
                filter { SpecialFileFilter(kind = SpecialFileFilter.Kind.JSON_SCHEMA) }
            }
            processor = object : Processor<Any> {
                override fun process(content: Any, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?) {
                    throw IllegalStateException("File name 'pattern.json' is invalid.\nThat name is reserved in JSON and has a special meaning " +
                            "that does not apply to this case.\nPlease remove or rename the invalid file.\nID of the broken entry: $identifier" )
                }
            }
        }
        "Registering defaults" {
            filters {
                filter { RegularFileFilter() }
                filter { JsonFileFilter() }
                filter {
                    object : Filter {
                        override fun canLoad(location: Location) =
                                location.path.fileName.endsWith("_defaults.json") && location.path.parent.parent.fileName.endsWith(MOD_ID)
                    }
                }
            }
            preprocessor = JsonConverterPreprocessor()
            processor = CatchingProcessor(logger = l, processor = RecipeLoadingProcessor(0))
        }
        "Registering factories" {
            filters {
                filter { RegularFileFilter() }
                filter { JsonFileFilter() }
                filter { SpecialFileFilter(kind = SpecialFileFilter.Kind.FACTORIES) }
            }
            preprocessor = CatchingPreprocessor(logger = l, preprocessor = JsonConverterPreprocessor())
            processor = CatchingProcessor(logger = l, processor = RecipeLoadingProcessor(1))
        }
        "Registering late-bound factories" {
            filters {
                filter { RegularFileFilter() }
                filter { JsonFileFilter() }
                filter {
                    object : Filter {
                        override fun canLoad(location: Location) =
                                location.path.fileName.endsWith("_late_bound.json") && location.path.parent.parent.fileName.endsWith(MOD_ID)
                    }
                }
            }
            preprocessor = JsonConverterPreprocessor()
            processor = CatchingProcessor(logger = l, processor = RecipeLoadingProcessor(2))
        }
        "Loading Constants" {
            filters {
                filter { RegularFileFilter() }
                filter { JsonFileFilter() }
                filter {
                    object : Filter {
                        override fun canLoad(location: Location) = location.path.fileName.endsWith("_constants.json")
                    }
                }
            }
            preprocessor = CatchingPreprocessor(logger = l, preprocessor = ConstantsPreprocessor())
            processor = CatchingProcessor(logger = l, processor = ConstantsLoadingProcessor())
        }
        "Registering recipes" {
            filters {
                filter { RegularFileFilter() }
                filter { JsonFileFilter() }
                filter { SpecialFileFilter(kind = SpecialFileFilter.Kind.UNDERSCORE_PREFIX, inverted = true) }
            }
            preprocessor = CatchingPreprocessor(logger = l, preprocessor = JsonConverterPreprocessor())
            processor = CatchingProcessor(logger = l, processor = RecipeLoadingProcessor(4))
        }
    }
}

private val assetsWarnerLoader = loader {
    name = "Recipes in Assets"
    identifierBuilder = DefaultIdentifierBuilder(removeExtension = true)
    progressVisitor = ProgressBarVisitor()
    locators {
        locator { ModContainerLocator(targetDirectory = "recipes", kind = ModContainerLocator.Kind.ASSET) }
    }
    phases {
        "Assets Checker" {
            filters {
                filter { RegularFileFilter() }
                filter { JsonFileFilter() }
                filter { SpecialFileFilter(kind = SpecialFileFilter.Kind.UNDERSCORE_PREFIX, inverted = true) }
            }
            processor = object : Processor<Any> {
                private val l = L(MOD_NAME, "Recipes in Assets")
                private val warnedMods = mutableListOf<String>()

                override fun process(content: Any, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?) {
                    if (!this.warnedMods.contains(identifier.nameSpace)) {
                        this.warnedMods += identifier.nameSpace
                        l.warn("The mod '${identifier.nameSpace}' is defining some recipes in its 'assets/' directory: these will not survive a 1.13+ upgrade. " +
                                "Please consider moving them to 'data/' instead")
                    }
                }
            }
        }
    }
}

internal fun loadDataPackRecipes() {
    l.info("Preparing to load recipes from data-packs")
    recipesLoader.load()
    l.info("Data-pack-based recipes loading has completed: now starting assets scanning")
    if (!common["recipes"]["suppress_update_warnings"]().boolean) {
        l.info("You can suppress this check via the configuration file")
        assetsWarnerLoader.load()
    }
    l.info("Scan completed")
}
