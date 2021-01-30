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

@file:JvmName("TCTTC")

package net.thesilkminer.mc.boson.prefab.tag

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.api.tag.Tag

infix fun Tag<ItemStack>.has(item: Item) = item isInTag this
infix fun Tag<ItemStack>.has(itemStack: ItemStack) = itemStack isInTag this
infix fun Tag<IBlockState>.has(block: Block) = block isInTag this
infix fun Tag<IBlockState>.has(blockState: IBlockState) = blockState isInTag this

infix fun Item.isInTag(tag: Tag<ItemStack>) = ItemStack(this, 1, 0) isInTag tag

infix fun ItemStack.isInTag(tag: Tag<ItemStack>) = tag.elements
        .asSequence()
        .filter { it.item == this.item }
        .filter { this.metadata == OreDictionary.WILDCARD_VALUE || it.metadata == this.metadata || it.metadata == OreDictionary.WILDCARD_VALUE }
        .any()

infix fun Block.isInTag(tag: Tag<IBlockState>) = this.defaultState isInTag tag

infix fun IBlockState.isInTag(tag: Tag<IBlockState>) = tag.elements
        .asSequence()
        .filter { it isSubBlockStateOf this }
        .any()

private infix fun IBlockState.isSubBlockStateOf(other: IBlockState): Boolean {
    if (this.block != other.block) return false
    if (this.properties.count() != other.properties.count()) return false
    this.propertyKeys.forEach {
        val thisProperty = this.properties[it] ?: return false
        val otherProperty = other.properties[it] ?: return false
        try {
            if (thisProperty.uncheckedCast<Comparable<Any>>().compareTo(otherProperty.uncheckedCast()) != 0) return false
        } catch (e: ClassCastException) {
            return false
        }
    }
    return true
}
