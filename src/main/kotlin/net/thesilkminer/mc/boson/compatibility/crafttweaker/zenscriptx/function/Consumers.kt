package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function

import crafttweaker.annotations.ZenRegister
import stanhebben.zenscript.annotations.ZenClass

@FunctionalInterface
@ZenClass("zenscriptx.function.Consumer")
@ZenRegister
interface Consumer<in T> {
    fun accept(t: T)
}

@FunctionalInterface
@ZenClass("zenscriptx.function.DoubleConsumer")
@ZenRegister
interface DoubleConsumer {
    fun accept(value: Double)
}

@FunctionalInterface
@ZenClass("zenscriptx.function.IntConsumer")
@ZenRegister
interface IntConsumer {
    fun accept(value: Int)
}

@FunctionalInterface
@ZenClass("zenscriptx.function.LongConsumer")
@ZenRegister
interface LongConsumer {
    fun accept(value: Long)
}
