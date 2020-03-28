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

package net.thesilkminer.mc.boson.mod.client

import net.minecraftforge.fml.common.Loader
import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.api.modid.BASE
import net.thesilkminer.mc.boson.api.resource.ResourcePackCreationManager
import net.thesilkminer.mc.boson.api.resource.ResourcePackCreationRequester
import java.nio.file.Paths

class BosonResourcePackRequester : ResourcePackCreationRequester {
    override fun apply(manager: ResourcePackCreationManager) {
        if (!Loader.isModLoaded(BASE)) {
            manager.request(
                    owner = MOD_ID,
                    name = "User-added Resources",
                    description = "Resource pack that gets automatically injected for additional resources",
                    root = Paths.get(".").resolve("./resources")
            )
        }
    }
}
