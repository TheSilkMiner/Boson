@file:Suppress("unused")

package net.thesilkminer.mc.boson.mod.common.recipe

import com.google.gson.JsonObject
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.JsonUtils
import net.minecraftforge.common.crafting.IIngredientFactory
import net.minecraftforge.common.crafting.JsonContext

class TagIngredientFactory : IIngredientFactory {
    override fun parse(context: JsonContext, json: JsonObject): Ingredient {
        // TODO()
        val tag = JsonUtils.getString(json, "tag")
        RecipeLoadingProcessor.l.warn("Tag API is not yet present: tags cannot be used")
        return Ingredient.EMPTY
    }
}
