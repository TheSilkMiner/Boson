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

package net.thesilkminer.mc.boson.prefab.loader.naming

import net.minecraftforge.fml.common.Loader
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.IdentifierBuilder
import net.thesilkminer.mc.boson.api.loader.Location
import net.thesilkminer.mc.boson.prefab.loader.modIdContextKey
import org.apache.commons.io.FilenameUtils

class DefaultIdentifierBuilder(private val removeExtension: Boolean = false) : IdentifierBuilder {
    override fun makeIdentifier(location: Location, globalContext: Context?, phaseContext: Context?) =
            bosonApi.constructNameSpacedString(
                    nameSpace = location.additionalContext?.get(modIdContextKey) ?: Loader.instance().activeModContainer()?.modId,
                    path = (if (this.removeExtension) FilenameUtils.removeExtension(location.path.toString()) else location.path.toString()).replace(Regex("\\\\"), "/")
            )
}
