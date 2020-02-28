@file:JvmName("LU")

package net.thesilkminer.mc.boson.compatibility.crafttweaker

import crafttweaker.runtime.ScriptLoader
import crafttweaker.runtime.events.CrTLoaderLoadingEvent
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.log.L

private val l = L("$MOD_NAME - CT Integration", "Script Loading Listener")

private var currentScriptLoader: ScriptLoader? = null

val currentLoaderName get() = currentScriptLoader?.mainName ?: "crafttweaker".also { l.bigWarn("No loader is currently in place: replacing with 'crafttweaker'! Check your calls!") }

object LoaderChangeListener {
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
