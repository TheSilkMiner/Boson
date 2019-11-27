@file:Suppress("unused")

package net.thesilkminer.mc.boson.mod.common.recipe

import com.google.gson.JsonObject
import net.minecraftforge.common.crafting.IConditionFactory
import net.minecraftforge.common.crafting.JsonContext
import java.util.function.BooleanSupplier

class TrueConditionFactory : IConditionFactory {
    override fun parse(context: JsonContext, json: JsonObject) = BooleanSupplier { true }
}
