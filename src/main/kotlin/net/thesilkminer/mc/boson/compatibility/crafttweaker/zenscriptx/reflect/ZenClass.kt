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

package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.reflect

import crafttweaker.annotations.ZenRegister
import crafttweaker.zenscript.GlobalRegistry
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenGetter
import stanhebben.zenscript.annotations.ZenMethod
import stanhebben.zenscript.type.ZenType

@ZenClass("zenscriptx.reflect.Class")
@ZenRegister
class ZenClass(private val targetZenType: ZenType) {

    companion object {
        @JvmStatic
        @ZenMethod("byName")
        fun getClassFrom(name: String): net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.reflect.ZenClass? =
                GlobalRegistry.getTypes().typeMap
                    .asSequence()
                    .map { it.value }
                    .find { it.name == name }
                    ?.let { ZenClass(it) }

        @JvmStatic
        @ZenMethod("from")
        fun getClassFrom(instance: Any): net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.reflect.ZenClass? =
                ZenNativeClass.getClassFromZen(instance)?.toZenClass()
    }

    val simpleName: String @ZenGetter("simpleName") get() = this.targetZenType.name.substringAfterLast('.')
    val qualifiedName: String @ZenGetter("qualifiedName") get() = this.targetZenType.name

    @ZenMethod("toNativeClass")
    fun toNativeClass(): ZenNativeClass? =
            GlobalRegistry.getTypes().typeMap
                    .asSequence()
                    .map { Pair(it.key.kotlin, it.value) }
                    .find { it.second == this.targetZenType }
                    ?.first
                    ?.let { ZenNativeClass(it) }
}
