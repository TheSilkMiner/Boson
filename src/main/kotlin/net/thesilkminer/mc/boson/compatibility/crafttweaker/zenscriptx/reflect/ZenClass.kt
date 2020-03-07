package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.reflect

import crafttweaker.annotations.ZenRegister
import crafttweaker.zenscript.GlobalRegistry
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenGetter
import stanhebben.zenscript.annotations.ZenMethod
import stanhebben.zenscript.type.ZenType

@ZenClass("zenscriptx.reflect.Class")
@ZenRegister
class ZenClass(private val targetZenType: ZenType) {

    companion object {
        @JvmStatic
        @ZenMethod("byName")
        fun getClassFrom(name: String): net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.reflect.ZenClass? =
                GlobalRegistry.getTypes().typeMap
                    .asSequence()
                    .map { it.value }
                    .find { it.name == name }
                    ?.let { ZenClass(it) }

        @JvmStatic
        @ZenMethod("from")
        fun getClassFrom(instance: Any): net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.reflect.ZenClass? =
                ZenNativeClass.getClassFromZen(instance)?.toZenClass()
    }

    val simpleName: String @ZenGetter("simpleName") get() = this.targetZenType.name.substringAfterLast('.')
    val qualifiedName: String @ZenGetter("qualifiedName") get() = this.targetZenType.name

    @ZenMethod("toNativeClass")
    fun toNativeClass(): ZenNativeClass? =
            GlobalRegistry.getTypes().typeMap
                    .asSequence()
                    .map { Pair(it.key.kotlin, it.value) }
                    .find { it.second == this.targetZenType }
                    ?.first
                    ?.let { ZenNativeClass(it) }
}
