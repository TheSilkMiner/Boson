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

fun NameSpacedString.toZen() = ZenNameSpacedString(this.nameSpace, this.path)
fun ZenNameSpacedString.toNative() = NameSpacedString(this.nameSpace, this.path)

fun <T : Any> Tag<T>.toZen() = ZenTag(this)
fun <T : Any> ZenTag<T>.toNative() = this.tag

fun <T : Any> TagType<T>.toZen() = ZenTagType(this)
fun <T : Any> ZenTagType<T>.toNative() = this.tagType

fun <T> Sequence<T>.toZen() = ZenSequence(this)
fun <T> ZenSequence<T>.toNative() = this.sequence

fun TagIngredient.toZen() = ZenTagIngredient(this)
fun ZenTagIngredient.toNative() = this.tagIngredient

fun <T : Any> KClass<T>.toZen() = ZenNativeClass(this)
fun <T : Any> ZenNativeClass.toNative() = this.nativeClass.uncheckedCast<KClass<T>>()
