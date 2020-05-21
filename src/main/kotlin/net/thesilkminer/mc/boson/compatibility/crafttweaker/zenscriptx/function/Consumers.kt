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
@ZenClass("zenscriptx.function.Consumer")
@ZenRegister
interface Consumer<in T> {
    @ZenMethod fun accept(t: T)
}

@FunctionalInterface
@ZenClass("zenscriptx.function.DoubleConsumer")
@ZenRegister
interface DoubleConsumer {
    @ZenMethod fun accept(value: Double)
}

@FunctionalInterface
@ZenClass("zenscriptx.function.IntConsumer")
@ZenRegister
interface IntConsumer {
    @ZenMethod fun accept(value: Int)
}

@FunctionalInterface
@ZenClass("zenscriptx.function.LongConsumer")
@ZenRegister
interface LongConsumer {
    @ZenMethod fun accept(value: Long)
}
