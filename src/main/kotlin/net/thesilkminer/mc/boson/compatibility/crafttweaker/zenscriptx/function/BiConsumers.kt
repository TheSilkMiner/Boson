package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function

import crafttweaker.annotations.ZenRegister
import stanhebben.zenscript.annotations.ZenClass

@FunctionalInterface
@ZenClass("zenscriptx.function.BiConsumer")
@ZenRegister
interface BiConsumer<in T, in U> {
    fun accept(t: T, u: U)
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ObjDoubleConsumer")
@ZenRegister
interface ObjDoubleConsumer<in T> {
    fun accept(t: T, value: Double)
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ObjIntConsumer")
@ZenRegister
interface ObjIntConsumer<in T> {
    fun accept(t: T, value: Int)
}

@FunctionalInterface
@ZenClass("zenscriptx.function.ObjLongConsumer")
@ZenRegister
interface ObjLongConsumer<in T> {
    fun accept(t: T, value: Long)
}
