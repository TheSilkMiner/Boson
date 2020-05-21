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

package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function

import crafttweaker.annotations.ZenRegister
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@FunctionalInterface
@ZenClass("zenscriptx.function.BiConsumer")
@ZenRegister
interface BiConsumer<in T, in U> {
    @ZenMethod fun accept(t: T, u: U)
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ObjDoubleConsumer")
@ZenRegister
interface ObjDoubleConsumer<in T> {
    @ZenMethod fun accept(t: T, value: Double)
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ObjIntConsumer")
@ZenRegister
interface ObjIntConsumer<in T> {
    @ZenMethod fun accept(t: T, value: Int)
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ObjLongConsumer")
@ZenRegister
interface ObjLongConsumer<in T> {
    @ZenMethod fun accept(t: T, value: Long)
}
