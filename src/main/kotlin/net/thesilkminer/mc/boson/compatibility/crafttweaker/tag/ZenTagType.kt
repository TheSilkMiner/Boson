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

package net.thesilkminer.mc.boson.compatibility.crafttweaker.tag

import crafttweaker.annotations.ZenRegister
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.tag.TagType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag.boxNative
import net.thesilkminer.mc.boson.compatibility.crafttweaker.naming.ZenNameSpacedString
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toNative
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toZen
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.Function
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenGetter

@ZenClass("net.thesilkminer.mc.boson.zen.tag.TagType")
@ZenRegister
class ZenTagType<T : Any>(val tagType: TagType<T>) {
    private class FunctionWrapper<T : Any, out R : Any>(private val targetFun: (NameSpacedString) -> T, private val tagType: TagType<T>) : Function<ZenNameSpacedString, R?> {
        override fun apply(t: ZenNameSpacedString): R? = this.targetFun(t.toNative()).boxNative(this.tagType)
    }

    companion object {
        fun <T : Any> findZenTagType(name: String) = (TagType.find<T>(name) ?: throw IllegalStateException("Unable to obtain tag type for unknown type '$name'")).toZen()
    }

    val directoryName: String @ZenGetter("directoryName") get() = this.tagType.directoryName
    val name: String @ZenGetter("name") get() = this.tagType.name
    val type: net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.reflect.ZenClass? @ZenGetter("classType") get() = this.tagType.type.toZen().toZenClass()
    val toElement: Function<ZenNameSpacedString, Any?> @ZenGetter("converterFunction") get() = this.tagType.toElement.toZen(this.tagType)

    private fun <T : Any, R : Any> ((NameSpacedString) -> T).toZen(tagType: TagType<T>) = FunctionWrapper<T, R>(this, tagType)
}
