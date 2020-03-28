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

@file:Suppress("unused")

package net.thesilkminer.mc.boson.mod.common.recipe

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.JsonUtils
import net.minecraftforge.common.crafting.IIngredientFactory
import net.minecraftforge.common.crafting.IngredientNBT
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.oredict.OreIngredient

class ItemDefaultIngredientFactory : IIngredientFactory {
    override fun parse(context: JsonContext, json: JsonObject): Ingredient {
        val item = JsonUtils.getString(json, "item")

        return if (item.startsWith("#")) {
            context.getConstant(item.substring(1)) ?: throw JsonSyntaxException("Referenced constant '$item' hasn't been defined")
        } else {
            Ingredient.fromStacks(RecipeLoadingProcessor.getItemStack(json, context, parseNbt = false))
        }
    }
}

class EmptyDefaultIngredientFactory : IIngredientFactory {
    override fun parse(context: JsonContext, json: JsonObject): Ingredient = Ingredient.EMPTY
}

class ItemWithNbtDefaultIngredientFactory : IIngredientFactory {
    private class NowPublicIngredientNbt(stack: ItemStack) : IngredientNBT(stack)

    override fun parse(context: JsonContext, json: JsonObject): Ingredient = NowPublicIngredientNbt(RecipeLoadingProcessor.getItemStack(json, context, parseNbt = true))
}

class OreDictionaryDefaultIngredientFactory : IIngredientFactory {
    override fun parse(context: JsonContext, json: JsonObject): Ingredient = OreIngredient(JsonUtils.getString(json, "ore"))
}
