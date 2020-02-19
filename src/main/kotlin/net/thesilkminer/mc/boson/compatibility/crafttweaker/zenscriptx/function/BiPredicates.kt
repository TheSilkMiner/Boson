package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function

import crafttweaker.annotations.ZenRegister
import stanhebben.zenscript.annotations.ZenClass

@FunctionalInterface
@ZenClass("zenscriptx.function.BiPredicate")
@ZenRegister
interface BiPredicate<in T, in U> {
    fun test(t: T, u: U): Boolean
}
