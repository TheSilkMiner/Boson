package net.thesilkminer.mc.boson.compatibility.crafttweaker.naming

import crafttweaker.annotations.ZenRegister
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toNative
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenGetter
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("net.thesilkminer.mc.boson.zen.naming.NameSpacedString")
@ZenRegister
class ZenNameSpacedString(@get:ZenGetter(value = "nameSpace") val nameSpace: String, @get:ZenGetter(value = "path") val path: String) {
    companion object {
        @JvmStatic
        @ZenMethod(value = "from")
        fun createFrom(nameSpace: String?, path: String) = ZenNameSpacedString(nameSpace ?: "minecraft", path)
    }

    @ZenMethod
    fun asString() = this.toNative().toString()
}
