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
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.Ingredient
import net.minecraft.item.crafting.ShapedRecipes
import net.minecraft.util.JsonUtils
import net.minecraft.util.NonNullList
import net.minecraftforge.common.crafting.IRecipeFactory
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.fml.common.registry.GameRegistry

class SmeltingRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        val group = JsonUtils.getString(json, "group", "")
        if (group.isNotEmpty() && group.indexOf(':') == -1) l.warn("Group '$group' does not have a name-space: this will not survive a 1.13+ upgrade")

        val ingredients = mutableListOf<Ingredient>()
        val jsonIngredient = json["ingredient"]

        if (jsonIngredient.isJsonArray) {
            val jsonIngredients = JsonUtils.getJsonArray(jsonIngredient, "ingredient")
            jsonIngredients.map { RecipeLoadingProcessor.getIngredient(it, context) }.forEach { ingredients += it }
        } else {
            ingredients += RecipeLoadingProcessor.getIngredient(JsonUtils.getJsonObject(jsonIngredient, "ingredient"), context)
        }

        val result = RecipeLoadingProcessor.getItemStack(JsonUtils.getJsonObject(json, "result"), context, parseNbt = true)
        val experience = JsonUtils.getFloat(json, "experience")
        val cookingTime = JsonUtils.getInt(json, "cookingtime", 200)

        if (group.isNotEmpty()) l.warn("Groups are not yet supported in 1.12: this will not work")
        if (ingredients.count() != 1) l.warn("Found multiple ingredients for a single recipe: this is not yet supported in 1.12, so they will be registered as separate recipes")
        if (cookingTime != 200) l.warn("Found cooking time to be $cookingTime: custom values are not yet supported in 1.12, so this will not work")

        ingredients.asSequence()
                .filter { it != Ingredient.EMPTY }
                .map { it.matchingStacks }
                .flatMap { it.asSequence() }
                .filter { !it.isEmpty }
                .filter { it.item != Items.AIR }
                .forEach { GameRegistry.addSmelting(it, result, experience) }

        return null
    }
}

class RemoveRecipeFactory : IRecipeFactory {
    override fun parse(context: JsonContext?, json: JsonObject?): IRecipe?
            = ShapedRecipes("", 1, 1, NonNullList.withSize(1, NoneIngredientFactory.NoneIngredient()), ItemStack(Items.AIR))
}
