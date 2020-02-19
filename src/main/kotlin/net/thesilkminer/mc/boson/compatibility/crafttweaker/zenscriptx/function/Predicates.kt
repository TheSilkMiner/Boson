package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function

import crafttweaker.annotations.ZenRegister
import stanhebben.zenscript.annotations.ZenClass

@FunctionalInterface
@ZenClass("zenscriptx.function.Predicate")
@ZenRegister
interface Predicate<in T> {
    fun test(t: T): Boolean
}

@FunctionalInterface
@ZenClass("zenscriptx.function.DoublePredicate")
@ZenRegister
interface DoublePredicate {
    fun test(value: Double): Boolean
}

@FunctionalInterface
@ZenClass("zenscriptx.function.IntPredicate")
@ZenRegister
interface IntPredicate {
    fun test(value: Int): Boolean
}

@FunctionalInterface
@ZenClass("zenscriptx.function.LongPredicate")
@ZenRegister
interface LongPredicate {
    fun test(value: Long): Boolean
}
