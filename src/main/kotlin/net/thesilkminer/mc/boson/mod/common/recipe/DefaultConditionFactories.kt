@file:Suppress("unused")

package net.thesilkminer.mc.boson.mod.common.recipe

import com.google.gson.JsonObject
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.crafting.IConditionFactory
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.registry.ForgeRegistries
import java.util.function.BooleanSupplier

class ModLoadedDefaultConditionFactory : IConditionFactory {
    override fun parse(context: JsonContext, json: JsonObject) = BooleanSupplier {
        Loader.isModLoaded(JsonUtils.getString(json, "mod_id"))
    }
}

class ItemExistsDefaultConditionFactory : IConditionFactory {
    override fun parse(context: JsonContext, json: JsonObject) = BooleanSupplier {
        ForgeRegistries.ITEMS.containsKey(ResourceLocation(context.appendModId(JsonUtils.getString(json, "item"))))
    }
}

class NotDefaultConditionFactory : IConditionFactory {
    override fun parse(context: JsonContext, json: JsonObject) = JsonUtils.getJsonObject(json, "value").let {
        BooleanSupplier {
            !RecipeLoadingProcessor.getCondition(it).parse(context, it).asBoolean
        }
    }
}

class OrDefaultConditionFactory : IConditionFactory {
    override fun parse(context: JsonContext, json: JsonObject) = BooleanSupplier {
        JsonUtils.getJsonArray(json, "values")
                .map { it.asJsonObject }
                .map { RecipeLoadingProcessor.getCondition(it).parse(context, it) }
                .map { it.asBoolean }
                .any { it }
    }
}

class AndDefaultConditionFactory : IConditionFactory {
    override fun parse(context: JsonContext, json: JsonObject) = BooleanSupplier {
        JsonUtils.getJsonArray(json, "values")
                .map { it.asJsonObject }
                .map { RecipeLoadingProcessor.getCondition(it).parse(context, it) }
                .map { it.asBoolean }
                .all { it }
    }
}

class FalseDefaultConditionFactory : IConditionFactory {
    override fun parse(context: JsonContext, json: JsonObject) = BooleanSupplier { false }
}
