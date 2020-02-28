@file:JvmName("WrapUnwrap")

package net.thesilkminer.mc.boson.compatibility.crafttweaker

import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.api.tag.TagType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.naming.ZenNameSpacedString
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTag
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTagIngredient
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTagType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.sequence.ZenSequence
import net.thesilkminer.mc.boson.mod.common.recipe.TagIngredient

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
