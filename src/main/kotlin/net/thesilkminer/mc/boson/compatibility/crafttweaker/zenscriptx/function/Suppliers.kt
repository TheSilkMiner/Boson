package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function

import crafttweaker.annotations.ZenRegister
import stanhebben.zenscript.annotations.ZenClass

@FunctionalInterface
@ZenClass("zenscriptx.function.Supplier")
@ZenRegister
interface Supplier<out T> {
    fun get(): T
}

@FunctionalInterface
@ZenClass("zenscriptx.function.BooleanSupplier")
@ZenRegister
interface BooleanSupplier {
    fun getAsBoolean(): Boolean
}

@FunctionalInterface
@ZenClass("zenscriptx.function.DoubleSupplier")
@ZenRegister
interface DoubleSupplier {
    fun getAsDouble(): Double
}

@FunctionalInterface
@ZenClass("zenscriptx.function.IntSupplier")
@ZenRegister
interface IntSupplier {
    fun getAsInt(): Int
}

@FunctionalInterface
@ZenClass("zenscriptx.function.LongSupplier")
@ZenRegister
interface LongSupplier {
    fun getAsLong(): Long
}
