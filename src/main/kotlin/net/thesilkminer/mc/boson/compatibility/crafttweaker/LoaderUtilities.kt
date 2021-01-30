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

@file:JvmName("LU")

package net.thesilkminer.mc.boson.compatibility.crafttweaker

import crafttweaker.runtime.ScriptLoader
import crafttweaker.runtime.events.CrTLoaderLoadingEvent
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.log.L

private val l = L("$MOD_NAME - CT Integration", "Script Loading Listener")

private var currentScriptLoader: ScriptLoader? = null

internal val currentLoaderName get() = currentScriptLoader?.mainName ?: "crafttweaker".also { l.bigWarn("No loader is currently in place: replacing with 'crafttweaker'! Check your calls!") }

internal object LoaderChangeListener {
    fun onLoaderBeginEvent(event: CrTLoaderLoadingEvent.Started) {
        l.debug("CraftTweaker has started loading with the loader '${event.loader.mainName}' (${event.loader})")
        currentScriptLoader = event.loader
    }

    fun onLoaderFinishedEvent(event: CrTLoaderLoadingEvent.Finished) {
        l.debug("CraftTweaker has finished loading with the loader '${event.loader.mainName}")
        currentScriptLoader = null
    }

    fun onLoaderAbortedEvent(event: CrTLoaderLoadingEvent.Aborted) {
        l.error("CraftTweaker loading with the loader '${event.loader.mainName}' aborted with message '${event.message}'!")
        currentScriptLoader = null
    }
}
