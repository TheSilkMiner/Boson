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

package net.thesilkminer.mc.boson.compatibility.top

import net.thesilkminer.mc.boson.api.modid.BOSON
import net.thesilkminer.mc.boson.api.modid.THE_ONE_PROBE
import net.thesilkminer.mc.boson.compatibility.BosonCompatibilityProvider
import net.thesilkminer.mc.boson.prefab.communication.FunctionMessage
import net.thesilkminer.mc.boson.prefab.compatibility.ModCompatibilityProvider

class TheOneProbeCompatibilityProvider : ModCompatibilityProvider(THE_ONE_PROBE), BosonCompatibilityProvider {
    override fun enqueueMessages() {
        FunctionMessage(sender = BOSON, key = "getTheOneProbe", content = TheOneProbePlugin::class).dispatchTo(THE_ONE_PROBE)
    }
}
