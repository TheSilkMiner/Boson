package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function

import crafttweaker.annotations.ZenRegister
import stanhebben.zenscript.annotations.ZenClass

@FunctionalInterface
@ZenClass("zenscriptx.function.BinaryOperator")
@ZenRegister
interface BinaryOperator<T> : BiFunction<T, T, T>

@FunctionalInterface
@ZenClass("zenscriptx.function.DoubleBinaryOperator")
@ZenRegister
interface DoubleBinaryOperator {
    fun applyAsDouble(left: Double, right: Double): Double
}

@FunctionalInterface
@ZenClass("zenscriptx.function.IntBinaryOperator")
@ZenRegister
interface IntBinaryOperator {
    fun applyAsDouble(left: Int, right: Int): Int
}

@FunctionalInterface
@ZenClass("zenscriptx.function.LongBinaryOperator")
@ZenRegister
interface LongBinaryOperator {
    fun applyAsDouble(left: Long, right: Long): Long
}
