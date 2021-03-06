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

package net.thesilkminer.mc.boson.mod.common.recipe

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntComparators
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.client.util.RecipeItemHelper
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.NonNullList
import net.minecraftforge.oredict.OreDictionary
import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.prefab.tag.isInTag

class TagIngredient(val tag: Tag<ItemStack>) : Ingredient() {
    private val items get() = this.tag.elements

    private var ids = null as IntList?
    private var matchingStacks = null as Array<ItemStack>?
    private var lastSizeForIds = -1
    private var lastSizeForStacks = -1

    override fun invalidate() {
        this.ids = null
        this.matchingStacks = null
        super.invalidate()
    }

    override fun apply(p_apply_1_: ItemStack?): Boolean {
        if (p_apply_1_ == null) return false
        return p_apply_1_ isInTag this.tag
    }

    override fun getValidItemStacksPacked(): IntList {
        if (this.ids == null || this.lastSizeForIds != this.items.count()) {
            val idList = IntArrayList(this.items.count())
            this.items.asSequence()
                    .map {
                        if (it.metadata == OreDictionary.WILDCARD_VALUE) {
                            val list = NonNullList.create<ItemStack>()
                            it.item.getSubItems(CreativeTabs.SEARCH, list)
                            list.asSequence()
                        } else {
                            sequenceOf(it)
                        }
                    }
                    .flatten()
                    .map(RecipeItemHelper::pack)
                    .forEach { idList.add(it) }
            idList.sortWith(IntComparators.NATURAL_COMPARATOR)
            this.ids = idList
            this.lastSizeForIds = this.items.count()
        }
        return this.ids!!
    }

    override fun isSimple() = true

    override fun getMatchingStacks(): Array<ItemStack> {
        if (this.matchingStacks == null || this.lastSizeForStacks != this.items.count()) {
            val list = NonNullList.create<ItemStack>()
            this.items.forEach {
                if (it.metadata == OreDictionary.WILDCARD_VALUE) {
                    it.item.getSubItems(CreativeTabs.SEARCH, list)
                } else {
                    list += it
                }
            }
            this.matchingStacks = list.toTypedArray()
            this.lastSizeForStacks = this.items.count()
        }
        return this.matchingStacks!!
    }
}

