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

@file:Suppress("unused")

package net.thesilkminer.mc.boson.mod.common.recipe

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.JsonUtils
import net.minecraftforge.common.crafting.IIngredientFactory
import net.minecraftforge.common.crafting.JsonContext
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.prefab.naming.toNameSpacedString
import net.thesilkminer.mc.boson.prefab.tag.itemTagType

class TagIngredientFactory : IIngredientFactory {
    override fun parse(context: JsonContext, json: JsonObject): Ingredient {
        val tag = JsonUtils.getString(json, "tag")
        if (tag.first() != '#') throw JsonSyntaxException("Tag name is invalid: does not begin with #")
        if (tag.substring(startIndex = 1).isEmpty()) throw JsonSyntaxException("Expected a tag name, but found none")
        val tagName = tag.substring(startIndex = 1).toNameSpacedString()
        val target = bosonApi.tagRegistry[itemTagType, tagName]
        return TagIngredient(target)
    }
}

class NoneIngredientFactory : IIngredientFactory {
    class NoneIngredient : Ingredient() {
        override fun apply(p_apply_1_: ItemStack?) = false
    }

    override fun parse(context: JsonContext?, json: JsonObject?): Ingredient = NoneIngredient()
}
