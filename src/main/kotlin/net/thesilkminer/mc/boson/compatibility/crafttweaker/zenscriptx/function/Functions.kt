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

@FunctionalInterface
@ZenClass("zenscriptx.function.Function")
@ZenRegister
interface Function<in T, out R> {
    fun apply(t: T): R
}

@FunctionalInterface
@ZenClass("zenscriptx.function.DoubleFunction")
@ZenRegister
interface DoubleFunction<out R> {
    fun apply(value: Double): R
}

@FunctionalInterface
@ZenClass("zenscriptx.function.DoubleToIntFunction")
@ZenRegister
interface DoubleToIntFunction {
    fun applyAsInt(value: Double): Int
}

@FunctionalInterface
@ZenClass("zenscriptx.function.DoubleToLongFunction")
@ZenRegister
interface DoubleToLongFunction {
    fun applyAsLong(value: Double): Long
}

@FunctionalInterface
@ZenClass("zenscriptx.function.IntFunction")
@ZenRegister
interface IntFunction<out R> {
    fun apply(value: Int): R
}

@FunctionalInterface
@ZenClass("zenscriptx.function.IntToDoubleFunction")
@ZenRegister
interface IntToDoubleFunction {
    fun applyAsDouble(value: Int): Double
}

@FunctionalInterface
@ZenClass("zenscriptx.function.IntToLongFunction")
@ZenRegister
interface IntToLongFunction {
    fun applyAsLong(value: Int): Long
}

@FunctionalInterface
@ZenClass("zenscriptx.function.LongFunction")
@ZenRegister
interface LongFunction<out R> {
    fun apply(value: Long): R
}

@FunctionalInterface
@ZenClass("zenscriptx.function.LongToDoubleFunction")
@ZenRegister
interface LongToDoubleFunction {
    fun applyAsDouble(value: Long): Double
}

@FunctionalInterface
@ZenClass("zenscriptx.function.LongToIntFunction")
@ZenRegister
interface LongToIntFunction {
    fun applyAsInt(value: Long): Int
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ToDoubleFunction")
@ZenRegister
interface ToDoubleFunction<in T> {
    fun apply(value: T): Double
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ToIntFunction")
@ZenRegister
interface ToIntFunction<in T> {
    fun apply(value: T): Int
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ToLongFunction")
@ZenRegister
interface ToLongFunction<in T> {
    fun apply(value: T): Long
}
