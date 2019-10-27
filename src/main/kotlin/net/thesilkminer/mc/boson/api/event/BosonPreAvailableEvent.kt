package net.thesilkminer.mc.boson.api.event

import net.minecraftforge.fml.common.LoaderState
import net.minecraftforge.fml.common.event.FMLStateEvent

/**
 * Latest event called in the lifecycle of mod loading.
 *
 * It is called after
 * [net.minecraftforge.fml.common.event.FMLPostInitializationEvent] and tells
 * the Mod Containers the loading process has completed for all mods and they
 * are transitioning to the `AVAILABLE` state.
 *
 * @param data
 *      Some data that may be needed to build the event.
 *
 * @since 1.0.0
 */
class BosonPreAvailableEvent(vararg data: Any) : FMLStateEvent(data) {
    override fun getModState() = LoaderState.ModState.POSTINITIALIZED
}
