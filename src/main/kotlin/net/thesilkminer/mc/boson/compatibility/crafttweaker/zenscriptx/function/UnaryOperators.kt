package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function

import crafttweaker.annotations.ZenRegister
import stanhebben.zenscript.annotations.ZenClass

@FunctionalInterface
@ZenClass("zenscriptx.function.UnaryOperator")
@ZenRegister
interface UnaryOperator<T> : Function<T, T>

@FunctionalInterface
@ZenClass("zenscriptx.function.DoubleUnaryOperator")
@ZenRegister
interface DoubleUnaryOperator {
    fun applyAsDouble(operand: Double): Double
}

@FunctionalInterface
@ZenClass("zenscriptx.function.IntUnaryOperator")
@ZenRegister
interface IntUnaryOperator {
    fun applyAsInt(operand: Int): Int
}

@FunctionalInterface
@ZenClass("zenscriptx.function.LongUnaryOperator")
@ZenRegister
interface LongUnaryOperator {
    fun applyAsLong(operand: Long): Long
}
