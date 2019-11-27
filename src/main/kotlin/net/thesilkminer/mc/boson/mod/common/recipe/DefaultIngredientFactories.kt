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
