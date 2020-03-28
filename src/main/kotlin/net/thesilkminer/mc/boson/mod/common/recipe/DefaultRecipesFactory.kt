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
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.Ingredient
import net.minecraft.item.crafting.ShapedRecipes
import net.minecraft.item.crafting.ShapelessRecipes
import net.minecraft.util.JsonUtils
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.crafting.IRecipeFactory
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.oredict.ShapedOreRecipe
import net.minecraftforge.oredict.ShapelessOreRecipe

class ShapedCraftingDefaultRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        val group = JsonUtils.getString(json, "group", "")
        if (group.isNotEmpty() && group.indexOf(':') == -1) l.warn("Group '$group' does not have a name-space: this will not survive a 1.13+ upgrade")

        val ingredientsMap = mutableMapOf<Char, Ingredient>()
        JsonUtils.getJsonObject(json, "key").entrySet().forEach {
            if (it.key.length != 1) throw JsonSyntaxException("Key '${it.key}' is invalid: not a single character")
            if (it.key.isBlank()) throw JsonSyntaxException("Key '${it.key}' is reserved")
            ingredientsMap[it.key.toCharArray()[0]] = RecipeLoadingProcessor.getIngredient(it.value, context)
        }
        ingredientsMap[' '] = Ingredient.EMPTY

        val pattern = JsonUtils.getJsonArray(json, "pattern")
        if (pattern.count() == 0) throw JsonSyntaxException("Empty pattern is not allowed")
        if (pattern.count() > 3) throw JsonSyntaxException("Pattern is too big: rows can only be at most 3, but found ${pattern.count()}")

        val stringPattern = Array(pattern.count()) { "" }
        (0 until stringPattern.count()).forEach {
            val line = JsonUtils.getString(pattern[it], "pattern[$it]")

            if (line.count() > 3) throw JsonSyntaxException("Pattern is too big: columns can only be at most 3, but found ${line.count()}")
            if (it > 0 && stringPattern[0].count() != line.count()) throw JsonSyntaxException("Pattern is invalid: each row must have the same amount of characters")

            stringPattern[it] = line
        }

        val input = NonNullList.withSize(stringPattern[0].count() * stringPattern.count(), Ingredient.EMPTY)
        val keys = mutableSetOf(*ingredientsMap.keys.toTypedArray())
        keys.remove(' ')

        var x = 0
        stringPattern.asSequence()
                .flatMap { it.toCharArray().asSequence() }
                .forEach {
                    val ingredient = ingredientsMap[it] ?: throw JsonSyntaxException("Pattern references symbol '$it', but it's undefined")
                    input[x++] = ingredient
                    keys.remove(it)
                }

        if (keys.isNotEmpty()) throw JsonSyntaxException("Symbols '$keys' are not used in the pattern")

        val result = RecipeLoadingProcessor.getItemStack(JsonUtils.getJsonObject(json, "result"), context, parseNbt = true)
        return ShapedRecipes(group, stringPattern[0].count(), stringPattern.count(), input, result)
    }
}

class ShapelessCraftingDefaultRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        val group = JsonUtils.getString(json, "group", "")
        if (group.isNotEmpty() && group.indexOf(':') == -1) l.warn("Group '$group' does not have a name-space: this will not survive a 1.13+ upgrade")

        val ingredients = NonNullList.create<Ingredient>()
        JsonUtils.getJsonArray(json, "ingredients").map { RecipeLoadingProcessor.getIngredient(it, context) }.forEach { ingredients.add(it) }

        if (ingredients.isEmpty()) throw JsonSyntaxException("Shapeless recipe must have at least one ingredient")
        if (ingredients.count() > 9) throw JsonSyntaxException("Too many items for shapeless recipe: expected at most 9, but found ${ingredients.count()}")

        return ShapelessRecipes(group, RecipeLoadingProcessor.getItemStack(JsonUtils.getJsonObject(json, "result"), context), ingredients)
    }
}

class ShapedOreCraftingDefaultRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    private data class ShapedPrimer(val height: Int, val width: Int, val inputs: NonNullList<Ingredient>, val mirrored: Boolean = true)

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        val group = JsonUtils.getString(json, "group", "")
        if (group.isNotEmpty() && group.indexOf(':') == -1) l.warn("Group '$group' does not have a name-space: this will not survive a 1.13+ upgrade")

        val ingredientsMap = mutableMapOf<Char, Ingredient>()
        JsonUtils.getJsonObject(json, "key").entrySet().forEach {
            if (it.key.length != 1) throw JsonSyntaxException("Key '${it.key}' is invalid: not a single character")
            if (it.key.isBlank()) throw JsonSyntaxException("Key '${it.key}' is reserved")
            ingredientsMap[it.key.toCharArray()[0]] = RecipeLoadingProcessor.getIngredient(it.value, context)
        }
        ingredientsMap[' '] = Ingredient.EMPTY

        val pattern = JsonUtils.getJsonArray(json, "pattern")
        if (pattern.count() == 0) throw JsonSyntaxException("Empty pattern is not allowed")

        val stringPattern = Array(pattern.count()) { "" }
        (0 until stringPattern.count()).forEach {
            val line = JsonUtils.getString(pattern[it], "pattern[$it]")

            if (it > 0 && stringPattern[0].count() != line.count()) throw JsonSyntaxException("Pattern is invalid: each row must have the same amount of characters")

            stringPattern[it] = line
        }

        val primer = ShapedPrimer(
                width = stringPattern[0].count(),
                height = stringPattern.count(),
                inputs = NonNullList.withSize(stringPattern[0].count() * stringPattern.count(), Ingredient.EMPTY),
                mirrored = JsonUtils.getBoolean(json, "mirrored", true)
        )

        val keys = mutableSetOf(*ingredientsMap.keys.toTypedArray())
        keys.remove(' ')

        var x = 0
        stringPattern.asSequence()
                .flatMap { it.toCharArray().asSequence() }
                .forEach {
                    val ingredient = ingredientsMap[it] ?: throw JsonSyntaxException("Pattern references symbol '$it', but it's undefined")
                    primer.inputs[x++] = ingredient
                    keys.remove(it)
                }

        if (keys.isNotEmpty()) throw JsonSyntaxException("Symbols '$keys' are not used in the pattern")

        val result = RecipeLoadingProcessor.getItemStack(JsonUtils.getJsonObject(json, "result"), context, parseNbt = true)
        return ShapedOreRecipe(if (group.isEmpty()) null else ResourceLocation(group), result, primer.toShapedOrePrimer())
    }

    private fun ShapedPrimer.toShapedOrePrimer() = net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer().let {
        it.width = this.width
        it.height = this.height
        it.input = this.inputs
        it.mirrored = this.mirrored
        it
    }
}

class ShapelessOreCraftingDefaultRecipeFactory : IRecipeFactory {
    companion object val l = RecipeLoadingProcessor.l

    override fun parse(context: JsonContext, json: JsonObject): IRecipe? {
        val group = JsonUtils.getString(json, "group", "")
        if (group.isNotEmpty() && group.indexOf(':') == -1) l.warn("Group '$group' does not have a name-space: this will not survive a 1.13+ upgrade")

        val ingredients = NonNullList.create<Ingredient>()
        JsonUtils.getJsonArray(json, "ingredients").map { RecipeLoadingProcessor.getIngredient(it, context) }.forEach { ingredients.add(it) }

        if (ingredients.isEmpty()) throw JsonSyntaxException("Shapeless recipe must have at least one ingredient")

        val result = RecipeLoadingProcessor.getItemStack(JsonUtils.getJsonObject(json, "result"), context)
        return ShapelessOreRecipe(if (group.isEmpty()) null else ResourceLocation(group), ingredients, result)
    }
}
