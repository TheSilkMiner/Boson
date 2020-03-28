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

package net.thesilkminer.mc.boson.compatibility.crafttweaker

import crafttweaker.CraftTweakerAPI
import crafttweaker.preprocessor.PreprocessorFactory
import crafttweaker.runtime.ScriptLoader
import crafttweaker.zenscript.GlobalRegistry
import net.thesilkminer.mc.boson.api.modid.CRAFT_TWEAKER_2
import net.thesilkminer.mc.boson.compatibility.BosonCompatibilityProvider
import net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.sequence.SequenceZenType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag.TagZenType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.preprocessor.ExperimentalFlagsHandler
import net.thesilkminer.mc.boson.compatibility.crafttweaker.preprocessor.ExperimentalFlagsPreprocessor
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTag
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.sequence.ZenSequence
import net.thesilkminer.mc.boson.prefab.compatibility.ModCompatibilityProvider
import stanhebben.zenscript.type.ZenType

class CraftTweakerCompatibilityProvider : ModCompatibilityProvider(CRAFT_TWEAKER_2), BosonCompatibilityProvider {
    companion object {
        const val TAG_LOADER_NAME = "tags"
        const val EXPERIMENTAL_FLAGS_PREPROCESSOR = "experimental"
    }

    private var tagsLoader: ScriptLoader? = null

    override fun onPreInitialization() {
        // Events
        CraftTweakerAPI.tweaker.registerLoadAbortedEvent(LoaderChangeListener::onLoaderAbortedEvent)
        CraftTweakerAPI.tweaker.registerLoadFinishedEvent(LoaderChangeListener::onLoaderFinishedEvent)
        CraftTweakerAPI.tweaker.registerLoadStartedEvent(LoaderChangeListener::onLoaderBeginEvent)
        CraftTweakerAPI.tweaker.registerScriptLoadPreEvent(ExperimentalFlagsHandler::onScriptLoadBeginEvent)
        CraftTweakerAPI.tweaker.registerScriptLoadPostEvent(ExperimentalFlagsHandler::onScriptLoadFinishedEvent)
        CraftTweakerAPI.tweaker.preprocessorManager.registerLoadEventHandler(ExperimentalFlagsHandler::onScriptLoadEvent)

        // Types
        GlobalRegistry.getTypes().typeMap.let {
            it[ZenSequence::class.java] = SequenceZenType(ZenType.ANY)
            it[ZenTag::class.java] = TagZenType(ZenType.ANY)
        }

        // Preprocessor
        CraftTweakerAPI.tweaker.preprocessorManager.registerPreprocessorAction(EXPERIMENTAL_FLAGS_PREPROCESSOR, PreprocessorFactory(ExperimentalFlagsPreprocessor.Companion::create))

        // Loaders
        this.tagsLoader = CraftTweakerAPI.tweaker.getOrCreateLoader(TAG_LOADER_NAME).apply { this.mainName = TAG_LOADER_NAME }
    }

    override fun onPostInitialization() {
        CraftTweakerAPI.tweaker.loadScript(false, this.tagsLoader ?: throw IllegalStateException("Tag loader cannot be null"))
    }
}
