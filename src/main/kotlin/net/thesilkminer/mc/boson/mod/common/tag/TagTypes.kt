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

package net.thesilkminer.mc.boson.mod.common.tag

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.tag.TagType
import net.thesilkminer.mc.boson.prefab.naming.toResourceLocation

private typealias NPE = KotlinNullPointerException

internal val blocks by lazy { TagType(IBlockState::class, "blocks") { it.findBlockState() } }
internal val fluids by lazy { TagType(Fluid::class, "fluids") { FluidRegistry.getFluid(it.path).n(it, "fluids") } } //TODO("Actually implement fluids correctly")
internal val items by lazy { TagType(ItemStack::class, "items", { it.findItemStack() }) { a: ItemStack, b: ItemStack -> a isEqualTo b } }

private fun NameSpacedString.findBlockState() = ForgeRegistries.BLOCKS.getValue(this.toResourceLocation()).n(this, "blocks").defaultState
private fun NameSpacedString.findItemStack() = ItemStack(ForgeRegistries.ITEMS.getValue(this.toResourceLocation()).n(this, "items"), 1, 0)

private infix fun ItemStack.isEqualTo(that: ItemStack) = this.item == that.item && this.metadata == that.metadata // TODO("Something else")

private fun <T> T?.n(a: NameSpacedString, t: String) = this ?: throw IllegalArgumentException("Tags of type '$t' don't support null entries, but '$a' was", NPE("null"))
