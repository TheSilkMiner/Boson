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

@file:JvmName("CTT")

package net.thesilkminer.mc.boson.prefab.tag

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.thesilkminer.mc.boson.api.tag.TagType

val blockTagType get() = TagType.find<IBlockState>("blocks") ?: throw NoSuchElementException("Tag type for 'blocks' wasn't registered")
val fluidTagType get() = TagType.find<Fluid>("fluids") ?: throw NoSuchElementException("Tag type for 'fluids' wasn't registered")
val itemTagType get() = TagType.find<ItemStack>("items") ?: throw NoSuchElementException("Tag type for 'items' wasn't registered")
