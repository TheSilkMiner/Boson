@file:JvmName("WrapUnwrap")

package net.thesilkminer.mc.boson.compatibility.crafttweaker

import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.api.tag.TagType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.naming.ZenNameSpacedString
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTag
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTagIngredient
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTagType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.reflect.ZenClass
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.reflect.ZenNativeClass
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.sequence.ZenSequence
import net.thesilkminer.mc.boson.mod.common.recipe.TagIngredient
import kotlin.reflect.KClass

internal fun NameSpacedString.toZen() = ZenNameSpacedString(this.nameSpace, this.path)
internal fun ZenNameSpacedString.toNative() = NameSpacedString(this.nameSpace, this.path)

internal fun <T : Any> Tag<T>.toZen() = ZenTag(this)
internal fun <T : Any> ZenTag<T>.toNative() = this.tag

internal fun <T : Any> TagType<T>.toZen() = ZenTagType(this)
internal fun <T : Any> ZenTagType<T>.toNative() = this.tagType

internal fun <T> Sequence<T>.toZen() = ZenSequence(this)
internal fun <T> ZenSequence<T>.toNative() = this.sequence

internal fun TagIngredient.toZen() = ZenTagIngredient(this)
internal fun ZenTagIngredient.toNative() = this.tagIngredient

internal fun <T : Any> KClass<T>.toZen() = ZenNativeClass(this)
internal fun <T : Any> ZenNativeClass.toNative() = this.nativeClass.uncheckedCast<KClass<T>>()
