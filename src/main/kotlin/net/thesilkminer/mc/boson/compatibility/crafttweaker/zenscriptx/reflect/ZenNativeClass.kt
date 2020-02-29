package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.reflect

import crafttweaker.annotations.ZenRegister
import crafttweaker.zenscript.GlobalRegistry
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenGetter
import stanhebben.zenscript.annotations.ZenMethod
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

@ZenClass("zenscriptx.reflect.NativeClass")
@ZenRegister
class ZenNativeClass(val nativeClass: KClass<*>) {

    companion object {
        @JvmStatic
        @ZenMethod("byName")
        fun getClassFor(name: String): ZenNativeClass? = try { ZenNativeClass(Class.forName(name).kotlin) } catch (e: ClassNotFoundException) { null }
    }

    val simpleName: String @ZenGetter("simpleName") get() = this.nativeClass.simpleName ?: this.nativeClass.java.simpleName
    val qualifiedName: String @ZenGetter("qualifiedName") get() = this.nativeClass.qualifiedName ?: this.nativeClass.java.name

    @ZenMethod("toClass")
    fun toZenClass(): net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.reflect.ZenClass? =
            GlobalRegistry.getTypes().typeMap
                    .asSequence()
                    .map { Pair(it.key.kotlin, it.value) }
                    .let { seq -> seq.find { it.first == this.nativeClass } ?: seq.find { it.first.isSuperclassOf(this.nativeClass) } }
                    ?.second
                    ?.let { ZenClass(it) }
}
