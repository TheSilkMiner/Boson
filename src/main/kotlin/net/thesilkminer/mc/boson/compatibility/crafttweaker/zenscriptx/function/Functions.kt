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
@ZenClass("zenscriptx.function.Function")
@ZenRegister
interface Function<in T, out R> {
    @ZenMethod fun apply(t: T): R
}

@FunctionalInterface
@ZenClass("zenscriptx.function.DoubleFunction")
@ZenRegister
interface DoubleFunction<out R> {
    @ZenMethod fun apply(value: Double): R
}

@FunctionalInterface
@ZenClass("zenscriptx.function.DoubleToIntFunction")
@ZenRegister
interface DoubleToIntFunction {
    @ZenMethod fun applyAsInt(value: Double): Int
}

@FunctionalInterface
@ZenClass("zenscriptx.function.DoubleToLongFunction")
@ZenRegister
interface DoubleToLongFunction {
    @ZenMethod fun applyAsLong(value: Double): Long
}

@FunctionalInterface
@ZenClass("zenscriptx.function.IntFunction")
@ZenRegister
interface IntFunction<out R> {
    @ZenMethod fun apply(value: Int): R
}

@FunctionalInterface
@ZenClass("zenscriptx.function.IntToDoubleFunction")
@ZenRegister
interface IntToDoubleFunction {
    @ZenMethod fun applyAsDouble(value: Int): Double
}

@FunctionalInterface
@ZenClass("zenscriptx.function.IntToLongFunction")
@ZenRegister
interface IntToLongFunction {
    @ZenMethod fun applyAsLong(value: Int): Long
}

@FunctionalInterface
@ZenClass("zenscriptx.function.LongFunction")
@ZenRegister
interface LongFunction<out R> {
    @ZenMethod fun apply(value: Long): R
}

@FunctionalInterface
@ZenClass("zenscriptx.function.LongToDoubleFunction")
@ZenRegister
interface LongToDoubleFunction {
    @ZenMethod fun applyAsDouble(value: Long): Double
}

@FunctionalInterface
@ZenClass("zenscriptx.function.LongToIntFunction")
@ZenRegister
interface LongToIntFunction {
    @ZenMethod fun applyAsInt(value: Long): Int
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ToDoubleFunction")
@ZenRegister
interface ToDoubleFunction<in T> {
    @ZenMethod fun apply(value: T): Double
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ToIntFunction")
@ZenRegister
interface ToIntFunction<in T> {
    @ZenMethod fun apply(value: T): Int
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ToLongFunction")
@ZenRegister
interface ToLongFunction<in T> {
    @ZenMethod fun apply(value: T): Long
}
