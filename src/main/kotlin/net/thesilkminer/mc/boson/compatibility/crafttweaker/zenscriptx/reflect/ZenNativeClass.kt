/*
 * Copyright (C) 2021  TheSilkMiner
 *
 * This file is part of Boson.
 *
 * Boson is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Boson is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Boson.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact information:
 * E-mail: thesilkminer <at> outlook <dot> com
 */

package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.reflect

import crafttweaker.annotations.ZenRegister
import crafttweaker.zenscript.GlobalRegistry
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenGetter
import stanhebben.zenscript.annotations.ZenMethod
import stanhebben.zenscript.type.ZenType
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

@ZenClass("zenscriptx.reflect.NativeClass")
@ZenRegister
class ZenNativeClass(val nativeClass: KClass<*>) {

    companion object {
        @JvmStatic
        @ZenMethod("byName")
        fun getClassFor(name: String): ZenNativeClass? = try { ZenNativeClass(Class.forName(name).kotlin) } catch (e: ClassNotFoundException) { null }

        @JvmStatic
        @ZenMethod("fromZen")
        fun getClassFromZen(instance: Any): ZenNativeClass? = getClassFor(instance::class.qualifiedName ?: instance::class.java.name)
    }

    val simpleName: String @ZenGetter("simpleName") get() = this.nativeClass.simpleName ?: this.nativeClass.java.simpleName
    val qualifiedName: String @ZenGetter("qualifiedName") get() = this.nativeClass.qualifiedName ?: this.nativeClass.java.name

    @ZenMethod("toClass")
    fun toZenClass(): net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.reflect.ZenClass? =
            GlobalRegistry.getTypes().typeMap
                    .asSequence()
                    .map { Pair(it.key.kotlin, it.value) }
                    .filter { it.first.isSuperclassOf(this.nativeClass) }
                    .toList()
                    .findNearestSuperClass(this.nativeClass)
                    ?.second
                    ?.let { ZenClass(it) }

    private fun List<Pair<KClass<*>, ZenType>>.findNearestSuperClass(targetClass: KClass<*>): Pair<KClass<*>, ZenType>? {
        if (this.isEmpty()) return null
        var valid = this[0] // not empty, so guaranteed to have at least one element
        this.forEach { if (it.first.isMoreSpecificThan(valid.first, targetClass)) valid = it }
        return valid
    }

    private fun KClass<*>.isMoreSpecificThan(other: KClass<*>, targetClass: KClass<*>): Boolean {
        // Quick check: if this is the same as targetClass it will surely be more specific than anything else
        if (this == targetClass) return true

        // Quick check #2: if this is the same as other, then the most specific it can be is like this
        if (this == other) return true

        // We know that this is the set of all superclasses of targetClass
        // Let's check whether this is a superclass of other
        val isThisSuperClassOfOther = this.isSuperclassOf(other)
        // Now the opposite check
        val isOtherSuperClassOfThis = other.isSuperclassOf(this)

        // If both of them are true, then this == other and it should have been caught before
        if (isThisSuperClassOfOther && isOtherSuperClassOfThis) throw IllegalStateException("'$this' super '$other' && '$other' super '$this'")

        // If this super other, then other is more specific
        if (isThisSuperClassOfOther) return false

        // If other super this, then this is more specific
        if (isOtherSuperClassOfThis) return true

        // If we are here, the two classes don't share the same hierarchy, so returning either works
        // We're going to keep other as more specific though in this case
        return false
    }
}
