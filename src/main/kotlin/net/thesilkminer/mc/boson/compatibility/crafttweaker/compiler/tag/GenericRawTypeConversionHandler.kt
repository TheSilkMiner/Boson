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

@file:JvmName("GRTCH")

package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import crafttweaker.api.block.IBlockState
import crafttweaker.api.item.IItemStack
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.api.tag.TagType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTag
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toNative
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toNativeStack
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toZen
import net.thesilkminer.mc.boson.prefab.tag.blockTagType
import net.thesilkminer.mc.boson.prefab.tag.itemTagType
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.util.ZenPosition
import kotlin.reflect.KClass

// KClass <-> KClass
private val converterBiMap = HashBiMap.create<KClass<*>, KClass<*>>().apply { this.populate() }

@JvmName("populate\$KClass\$KClass")
private fun BiMap<KClass<*>, KClass<*>>.populate() {
    this[IItemStack::class] = ItemStack::class
    this[IBlockState::class] = net.minecraft.block.state.IBlockState::class
    // TODO("fluids")
    // TODO("everything else that may require custom handling")
}

internal fun KClass<*>.convertToZenGenericType() = converterBiMap.inverse()[this] ?: this.warn()
internal fun KClass<*>.convertToNativeGenericType() = converterBiMap[this] ?: this.warn()

private fun KClass<*>.warn() = this/*.also { l.info("Unable to map '$this': returning the same data and hoping") }*/

// Type -> KClass
private val typeToZenClass = HashBiMap.create<String, KClass<*>>().apply { this.populate() }

@JvmName("populate\$String\$KClass")
private fun BiMap<String, KClass<*>>.populate() {
    this[itemTagType.name] = IItemStack::class
    this[blockTagType.name] = IBlockState::class
    // TODO("fluids")
    // TODO("everything else that may require custom handling")
}

internal fun String.tryGetCustomClass(position: ZenPosition, environment: IEnvironmentGlobal) = typeToZenClass[this]
        ?: Object::class.also { environment.warning(position, "Tag type '$this' isn't fully supported yet: generic features won't be available") }

// native -> zen
private val nativeZenConverters = mutableMapOf<TagType<*>, (Any?) -> Any?>().apply { this.populateNz() }

@JvmName("populate\$TagType\$KFunction1\$nz")
private fun MutableMap<TagType<*>, (Any?) -> Any?>.populateNz() {
    this[itemTagType] = { (it as ItemStack).toZen(wildcard = it.metadata == OreDictionary.WILDCARD_VALUE) }
    this[blockTagType] = { (it as net.minecraft.block.state.IBlockState).toZen() }
    // TODO("fluids")
    // TODO("everything else that may require custom handling")
}

internal fun <T : Any, R : Any> T?.boxNative(tagType: TagType<T>): R? = (nativeZenConverters[tagType] ?: { it })(this)?.uncheckedCast()

// zen -> native
private val zenNativeConverters = mutableMapOf<TagType<*>, (Any?) -> Any?>().apply { this.populateZn() }

@JvmName("populate\$TagType\$KFunction1\$zn")
private fun MutableMap<TagType<*>, (Any?) -> Any?>.populateZn() {
    this[itemTagType] = { (it as IItemStack).toNativeStack() }
    this[blockTagType] = { (it as IBlockState).toNative() }
    // TODO("fluids")
    // TODO("everything else that may require custom handling")
}

internal fun <T : Any, R : Any> T?.unboxNative(tagType: TagType<T>): R? = (zenNativeConverters[tagType] ?: { it })(this)?.uncheckedCast()

// Other helpers
internal infix fun <T : Any> ZenTag<T>.isCompatibleWith(other: ZenTag<*>) = this.toNative().type.type == other.toNative().type.type

internal infix fun <T : Any> Array<*>.canFitIn(other: ZenTag<T>) = this.all { it != null && it::class.convertToNativeGenericType() == other.toNative().type.type }
