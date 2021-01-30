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
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.JsonUtils
import net.minecraftforge.common.crafting.IRecipeFactory
import net.minecraftforge.common.crafting.JsonContext

class BlastingLateBoundRecipeFactory : IRecipeFactory {
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
        val cookingTime = JsonUtils.getInt(json, "cookingtime", 100)

        l.warn("Recipe types 'minecraft:blasting' are not yet supported in 1.12: this recipe will not be registered")

        return null
    }
}

class CampfireCookingLateBoundRecipeFactory : IRecipeFactory {
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
        val cookingTime = JsonUtils.getInt(json, "cookingtime", 100)

        if (cookingTime == 100) l.warn("The custom value for cooking $cookingTime may not be respected in the actual implementation")
        l.warn("Recipe types 'minecraft:campfire_cooking' are not yet supported in 1.12: this recipe will not be registered")

        return null
    }
}

class CraftingSpecialArmorDyeLateBoundRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        l.warn("This recipe type ('minecraft:crafting_special_armordye') is already enabled and cannot be disabled in 1.12: this declaration is useless")
        return null
    }
}

class CraftingSpecialBannerDuplicateLateBoundRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        l.warn("This recipe type ('minecraft:crafting_special_bannerduplicate') is already enabled and cannot be disabled in 1.12: this declaration is useless")
        return null
    }
}

class CraftingSpecialBookCloningLateBoundRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        l.warn("This recipe type ('minecraft:crafting_special_bookcloning') is already enabled and cannot be disabled in 1.12: this declaration is useless")
        return null
    }
}

class CraftingSpecialFireworkRocketLateBoundRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        l.warn("This recipe type ('minecraft:crafting_special_firework_rocket') is already enabled and cannot be disabled in 1.12: this declaration is useless")
        return null
    }
}

class CraftingSpecialFireworkStarLateBoundRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        l.warn("This recipe type ('minecraft:crafting_special_firework_star') is already enabled and cannot be disabled in 1.12: this declaration is useless")
        return null
    }
}

class CraftingSpecialFireworkStarFadeLateBoundRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        l.warn("This recipe type ('minecraft:crafting_special_firework_star_fade') is already enabled and cannot be disabled in 1.12: this declaration is useless")
        return null
    }
}

class CraftingSpecialMapCloningLateBoundRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        l.warn("This recipe type ('minecraft:crafting_special_mapcloning') is already enabled and cannot be disabled in 1.12: this declaration is useless")
        return null
    }
}

class CraftingSpecialMapExtendingLateBoundRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        l.warn("This recipe type ('minecraft:crafting_special_mapextending') is already enabled and cannot be disabled in 1.12: this declaration is useless")
        return null
    }
}

class CraftingSpecialRepairItemLateBoundRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        l.warn("This recipe type ('minecraft:crafting_special_repairitem') is already enabled and cannot be disabled in 1.12: this declaration is useless")
        return null
    }
}

class CraftingSpecialShieldDecorationLateBoundRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        l.warn("This recipe type ('minecraft:crafting_special_shielddecoration') is already enabled and cannot be disabled in 1.12: this declaration is useless")
        return null
    }
}

class CraftingSpecialShulkerBoxColoringLateBoundRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        l.warn("This recipe type ('minecraft:crafting_special_shulkerboxcoloring') is already enabled and cannot be disabled in 1.12: this declaration is useless")
        return null
    }
}

class CraftingSpecialTippedArrowLateBoundRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        l.warn("This recipe type ('minecraft:crafting_special_tippedarrow') is already enabled and cannot be disabled in 1.12: this declaration is useless")
        return null
    }
}

class CraftingSpecialSuspiciousStewLateBoundRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        l.warn("This recipe type ('minecraft:crafting_special_suspiciousstew') is already enabled and cannot be disabled in 1.12: this declaration is useless")
        return null
    }
}

class SmokingLateBoundRecipeFactory : IRecipeFactory {
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

        l.warn("Recipe types 'minecraft:smoking' are not yet supported in 1.12: this recipe will not be registered")

        return null
    }
}

class StoneCuttingLateBoundRecipeFactory : IRecipeFactory {
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
        val count = JsonUtils.getInt(json, "count")

        l.warn("Recipe types 'minecraft:stonecutting' are not yet supported in 1.12: this recipe will not be registered")

        return null
    }
}
