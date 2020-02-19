@file:JvmName("StrExp")
@file:ZenExpansion("string")
@file:ZenRegister

package net.thesilkminer.mc.boson.compatibility.crafttweaker.naming

import crafttweaker.annotations.ZenRegister
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toZen
import net.thesilkminer.mc.boson.prefab.naming.toNameSpacedString
import stanhebben.zenscript.annotations.ZenCaster
import stanhebben.zenscript.annotations.ZenExpansion

@ZenCaster
fun toNameSpacedString(string: String) = string.toNameSpacedString().toZen()
