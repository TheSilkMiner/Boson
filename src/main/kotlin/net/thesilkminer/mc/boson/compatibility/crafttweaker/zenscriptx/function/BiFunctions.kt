package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function

import crafttweaker.annotations.ZenRegister
import stanhebben.zenscript.annotations.ZenClass

@FunctionalInterface
@ZenClass("zenscriptx.function.BiFunction")
@ZenRegister
interface BiFunction<in T, in U, out R> {
    fun apply(t: T, u: U): R
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ToDoubleBiFunction")
@ZenRegister
interface ToDoubleBiFunction<in T, in U> {
    fun apply(t: T, u: U): Double
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ToIntBiFunction")
@ZenRegister
interface ToIntBiFunction<in T, in U> {
    fun apply(t: T, u: U): Int
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ToLongBiFunction")
@ZenRegister
interface ToLongBiFunction<in T, in U> {
    fun apply(t: T, u: U): Long
}
