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
