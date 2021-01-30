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
