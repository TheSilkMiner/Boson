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

package net.thesilkminer.mc.boson.compatibility.crafttweaker.naming

import crafttweaker.annotations.ZenRegister
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toNative
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenGetter
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("net.thesilkminer.mc.boson.zen.naming.NameSpacedString")
@ZenRegister
class ZenNameSpacedString(@get:ZenGetter(value = "nameSpace") val nameSpace: String, @get:ZenGetter(value = "path") val path: String) {
    companion object {
        @JvmStatic
        @ZenMethod(value = "from")
        fun createFrom(nameSpace: String?, path: String) = ZenNameSpacedString(nameSpace ?: "minecraft", path)
    }

    @ZenMethod
    fun asString() = this.toNative().toString()

    override fun toString() = this.asString()
}
