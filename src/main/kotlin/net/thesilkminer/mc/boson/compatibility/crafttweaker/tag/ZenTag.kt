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

    // TODO("Use actions")

    fun add(vararg elements: ZenNameSpacedString) = this.tag.add(*elements.map { it.toNative() }.toTypedArray())
    fun addAll(elements: Array<*>) = if (elements canFitIn this) this.tag.add(elements.convertToSet()) else Unit
    fun addFrom(other: ZenTag<*>) = if (this isCompatibleWith other) this.tag.addFrom(other.toNative().uncheckedCast()) else Unit

    fun replace(vararg elements: ZenNameSpacedString) = this.tag.replace(*elements.map { it.toNative() }.toTypedArray())
    fun replaceAll(elements: Array<*>) = if (elements canFitIn this) this.tag.replace(elements.convertToSet()) else Unit
    fun replaceWith(other: ZenTag<*>) = if (this isCompatibleWith other) this.tag.replaceWith(other.toNative().uncheckedCast()) else Unit

    fun remove(vararg elements: ZenNameSpacedString) = this.tag.remove(*elements.map { it.toNative() }.toTypedArray())
    fun removeAll(elements: Array<*>) = if (elements canFitIn this) this.tag.remove(elements.convertToSet()) else Unit
    fun removeFrom(other: ZenTag<*>) = if (this isCompatibleWith other) this.tag.removeFrom(other.toNative().uncheckedCast()) else Unit

    fun clear() = this.tag.clear()

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
    private fun Array<*>.convertToSet() = this.map { this.unboxNative<Any, T>(this@ZenTag.type.toNative()) }.requireNoNulls().toSet()
}
