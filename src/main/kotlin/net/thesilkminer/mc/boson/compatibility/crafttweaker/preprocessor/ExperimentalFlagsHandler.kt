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

val flagsForCurrentScript: List<ExperimentalFlag> get() = (scriptToMap[currentScript?.effectiveName ?: ""] ?: { listOf() })()

fun attachFlags(file: ScriptFile?, flags: List<ExperimentalFlag>) {
    if (file == null) throw IllegalArgumentException("Attempted to set flags for a non-existent script file: this is interesting")
    scriptToMap[file.effectiveName]?.let { l.debug("Experimental flags for mapping '${file.effectiveName}' are being replaced from '${it()}' to '$flags': is this intended?") }
    scriptToMap[file.effectiveName] = { flags }
}

object ExperimentalFlagsHandler {
    fun onScriptLoadBeginEvent(event: CrTScriptLoadingEvent.Pre) {
        l.debug("CraftTweaker is about to start loading a script from file '${event.fileName}'")
        if (currentScript != null) throw IllegalStateException("Attempting to load a script while another one was loading! Invariants have been broken!")
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
