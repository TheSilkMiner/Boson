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

@file:JvmName("EFH")

package net.thesilkminer.mc.boson.compatibility.crafttweaker.preprocessor

import crafttweaker.preprocessor.CrTScriptLoadEvent
import crafttweaker.runtime.ScriptFile
import crafttweaker.runtime.events.CrTScriptLoadingEvent
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.log.L

private val l = L("$MOD_NAME - CT Integration", "Experimental Flags Handler")

private val scriptToMap = mutableMapOf<String, () -> List<ExperimentalFlag>>().apply { this[""] = { throw IllegalStateException("No script is currently loading!") } }

private var currentScript: ScriptFile? = null

internal val flagsForCurrentScript: List<ExperimentalFlag> get() = (scriptToMap[currentScript?.effectiveName ?: ""] ?: { listOf() })()

internal fun attachFlags(file: ScriptFile?, flags: List<ExperimentalFlag>) {
    if (file == null) throw IllegalArgumentException("Attempted to set flags for a non-existent script file: this is interesting")
    scriptToMap[file.effectiveName]?.let { l.debug("Experimental flags for mapping '${file.effectiveName}' are being replaced from '${it()}' to '$flags': is this intended?") }
    scriptToMap[file.effectiveName] = { flags }
}

internal object ExperimentalFlagsHandler {
    fun onScriptLoadBeginEvent(event: CrTScriptLoadingEvent.Pre) {
        l.debug("CraftTweaker is about to start loading a script from file '${event.fileName}'")
        if (currentScript != null) {
            l.bigError("""
                Attempting to load a script while another one was loading! Invariants have been broken! Attempting to correct: note that this may break further down the line
                Current Script: $currentScript
                New file: ${event.fileName}
            """.trimIndent())
            currentScript = null
        }
    }

    fun onScriptLoadEvent(event: CrTScriptLoadEvent) {
        l.debug("CraftTweaker has started loading the script '${event.scriptFile}'")
        currentScript = event.scriptFile
    }

    fun onScriptLoadFinishedEvent(event: CrTScriptLoadingEvent.Post) {
        l.debug("CraftTweaker has finished loading the script '$currentScript' ('${event.fileName}')")
        currentScript = null
    }
}
