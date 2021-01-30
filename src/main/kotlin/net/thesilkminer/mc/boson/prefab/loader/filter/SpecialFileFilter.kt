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

package net.thesilkminer.mc.boson.prefab.loader.filter

import net.thesilkminer.mc.boson.api.loader.Filter
import net.thesilkminer.mc.boson.api.loader.Location
import java.nio.file.Path

class SpecialFileFilter(private val kind: Kind, private val inverted: Boolean = false) : Filter {

    enum class Kind(val matcher: (Path) -> Boolean) {
        FACTORIES({ it.fileName.toString().startsWith("_factories") }),
        JSON_SCHEMA({ it.fileName.toString() == "pattern.json" }),
        UNDERSCORE_PREFIX({ it.fileName.toString().startsWith("_") })
    }

    override fun canLoad(location: Location) = with (this.kind.matcher(location.path)) { if (this@SpecialFileFilter.inverted) !this else this }
}
