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

package net.thesilkminer.mc.boson.compatibility.dymm

import com.aaronhowser1.dymm.JsonUtilities
import com.aaronhowser1.dymm.api.documentation.Target
import com.aaronhowser1.dymm.api.loading.GlobalLoadingState
import com.aaronhowser1.dymm.api.loading.factory.TargetFactory
import com.aaronhowser1.dymm.module.base.BasicItemTarget
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.prefab.naming.toNameSpacedString
import net.thesilkminer.mc.boson.prefab.tag.itemTagType

@Suppress("unused")
class TagTargetFactory : TargetFactory {
    override fun fromJson(state: GlobalLoadingState, `object`: JsonObject): List<Target> {
        val tagName = JsonUtilities.getString(`object`, "tag").normalize().toNameSpacedString()
        val tag = bosonApi.tagRegistry[itemTagType, tagName]
        return tag.elements.map { BasicItemTarget(it) }.toList()
    }

    private fun String.normalize() = if (!this.startsWith("#")) throw JsonSyntaxException("For string '$this': tag name must begin with '#'") else this.removePrefix("#")
}
