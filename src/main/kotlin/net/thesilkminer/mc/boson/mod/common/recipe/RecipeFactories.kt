@file:Suppress("unused")

package net.thesilkminer.mc.boson.mod.common.recipe

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.init.Items
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.JsonUtils
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
