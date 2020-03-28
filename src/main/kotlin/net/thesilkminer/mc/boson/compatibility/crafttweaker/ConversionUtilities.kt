/*
 * Copyright (C) 2020  TheSilkMiner
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
