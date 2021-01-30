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

package net.thesilkminer.mc.boson.compatibility.crafttweaker.tag

import crafttweaker.annotations.ZenRegister
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.api.tag.TagType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag.boxNative
import net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag.canFitIn
import net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag.isCompatibleWith
import net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag.unboxNative
import net.thesilkminer.mc.boson.compatibility.crafttweaker.ctAction
import net.thesilkminer.mc.boson.compatibility.crafttweaker.naming.ZenNameSpacedString
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toNative
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toZen
import net.thesilkminer.mc.boson.prefab.naming.toNameSpacedString
import stanhebben.zenscript.annotations.ZenClass

@ZenClass("net.thesilkminer.mc.boson.zen.tag.Tag")
@ZenRegister
class ZenTag<T : Any>(val tag: Tag<T>) {
    companion object {
        fun <T : Any> createAndWrap(tagType: TagType<T>, tagName: String) = ZenTag(bosonApi.tagRegistry[tagType, tagName.toNameSpacedString()])
    }

    val name: ZenNameSpacedString get() = this.tag.name.toZen()
    val type: ZenTagType<T> get() = this.tag.type.toZen()

    fun <R : Any> getElements() = this.tag.elements.copy().map { it.boxNative<T, R>(this.tag.type) }.toList()

    fun add(vararg elements: ZenNameSpacedString) = ctAction(this.a(elements.toList())) { this.tag.add(*elements.map { it.toNative() }.toTypedArray()) }
    fun addAll(elements: Array<*>) = ctAction(this.a("${elements.count()} elements")) { if (elements canFitIn this) this.tag.add(elements.convertToSet()) else Unit }
    fun addFrom(other: ZenTag<*>) = ctAction(this.a(other.name, true)) { if (this isCompatibleWith other) this.tag.addFrom(other.toNative().uncheckedCast()) else Unit }

    fun replace(vararg elements: ZenNameSpacedString) = ctAction(this.r(elements.toList())) { this.tag.replace(*elements.map { it.toNative() }.toTypedArray()) }
    fun replaceAll(elements: Array<*>) = ctAction(this.r("${elements.count()} elements")) { if (elements canFitIn this) this.tag.replace(elements.convertToSet()) else Unit }
    fun replaceWith(other: ZenTag<*>) = ctAction(this.r(other.name, true)) { if (this isCompatibleWith other) this.tag.replaceWith(other.toNative().uncheckedCast()) else Unit }

    fun remove(vararg elements: ZenNameSpacedString) = ctAction(this.d(elements.toList())) {this.tag.remove(*elements.map { it.toNative() }.toTypedArray()) }
    fun removeAll(elements: Array<*>) = ctAction(this.d("${elements.count()} elements")) { if (elements canFitIn this) this.tag.remove(elements.convertToSet()) else Unit }
    fun removeFrom(other: ZenTag<*>) = ctAction(this.d(other.name, true)) { if (this isCompatibleWith other) this.tag.removeFrom(other.toNative().uncheckedCast()) else Unit }

    fun clear() = ctAction("Clearing contents of tag #${this.name}") { this.tag.clear() }

    operator fun contains(element: T) = element in this.tag
    operator fun plusAssign(element: T) = this.addAll(arrayOf<Any>(element))
    operator fun plusAssign(elements: Array<*>) = elements.filterNotNull().forEach { if (it is ZenNameSpacedString) this.add(it) else this.addAll(arrayOf(it)) }
    operator fun plusAssign(element: ZenNameSpacedString) = this.add(element)
    operator fun plusAssign(other: ZenTag<out T>) = this.addFrom(other)
    operator fun minusAssign(element: T) = this.removeAll(arrayOf<Any>(element))
    operator fun minusAssign(elements: Array<*>) = elements.filterNotNull().forEach { if (it is ZenNameSpacedString) this.remove(it) else this.removeAll(arrayOf(it)) }
    operator fun minusAssign(element: ZenNameSpacedString) = this.remove(element)
    operator fun minusAssign(other: ZenTag<out T>) = this.removeFrom(other)
    operator fun unaryMinus() = this.clear()
    operator fun <R : Any> invoke() = this.getElements<R>()

    private fun Set<T>.copy() = this.toSet()
    private fun Array<*>.convertToSet() = this.map { this.unboxNative<Any, T>(this@ZenTag.type.toNative().uncheckedCast()) }.requireNoNulls().toSet()

    private fun a(s: Any, tr: Boolean = false) = "Adding ${this.tr(tr)}$s to tag #${this.name}"
    private fun r(s: Any, tr: Boolean = false) = "Replacing elements of #${this.name} with ${this.tr(tr)}$s"
    private fun d(s: Any, tr: Boolean = false) = "Removing ${this.tr(tr)}$s from tag #${this.name}"
    private fun tr(tr: Boolean) = if (tr) "tag reference #" else ""
}
