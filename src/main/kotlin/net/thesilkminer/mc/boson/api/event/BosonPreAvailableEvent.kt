package net.thesilkminer.mc.boson.api.event

import net.minecraftforge.fml.common.LoaderState
import net.minecraftforge.fml.common.event.FMLStateEvent

/**
 * // TODO
 *
 * @param data // TODO
 *
 * @since 1.0.0
 */
class BosonPreAvailableEvent(vararg data: Any) : FMLStateEvent(data) {
    override fun getModState() = LoaderState.ModState.POSTINITIALIZED
}
