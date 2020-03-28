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
