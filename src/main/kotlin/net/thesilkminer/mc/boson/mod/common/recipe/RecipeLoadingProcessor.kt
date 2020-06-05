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

package net.thesilkminer.mc.boson.mod.common.recipe

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.Ingredient
import net.minecraft.nbt.JsonToNBT
import net.minecraft.nbt.NBTException
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.crafting.CompoundIngredient
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.IConditionFactory
import net.minecraftforge.common.crafting.IIngredientFactory
import net.minecraftforge.common.crafting.IRecipeFactory
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.thesilkminer.kotlin.commons.lang.rethrowAs
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.Preprocessor
import net.thesilkminer.mc.boson.api.loader.Processor
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.prefab.loader.processor.CatchingProcessor
import net.thesilkminer.mc.boson.prefab.naming.toNameSpacedString
import kotlin.reflect.full.cast
import kotlin.reflect.full.createInstance

// This is not internal so that getCondition, getIngredient, getItemStack can be used by others
class RecipeLoadingProcessor(private val flags: Int) : Processor<JsonObject> {
    companion object {
        private const val INGREDIENTS = "ingredients"
        private const val RECIPES = "recipes"
        private const val CONDITIONS = "conditions"

        val l = L(MOD_NAME, "Recipes Processor")
        private var gc = null as Context?

        private val nameToInterface = mapOf(
                INGREDIENTS to IIngredientFactory::class,
                RECIPES to IRecipeFactory::class,
                CONDITIONS to IConditionFactory::class
        )
        private val nameToKey = mapOf(
                INGREDIENTS to bosonApi.createLoaderContextKey(INGREDIENTS, mutableMapOf<NameSpacedString, IIngredientFactory>()::class),
                RECIPES to bosonApi.createLoaderContextKey(RECIPES, mutableMapOf<NameSpacedString, IRecipeFactory>()::class),
                CONDITIONS to bosonApi.createLoaderContextKey(CONDITIONS, mutableMapOf<NameSpacedString, IConditionFactory>()::class)
        )

        val contextMapKey = bosonApi.createLoaderContextKey("context_map", mutableMapOf<String, JsonContext>()::class)

        fun getCondition(jsonObject: JsonObject) = getCondition(JsonUtils.getString(jsonObject, "type").toNameSpacedString())
        fun getIngredient(jsonElement: JsonElement, jsonContext: JsonContext) = when {
            jsonElement.isJsonArray -> this.getIngredientFromArray(jsonElement.asJsonArray, jsonContext)
            jsonElement.isJsonObject -> this.getIngredientFromObject(jsonElement.asJsonObject, jsonContext)
            else -> throw JsonSyntaxException("Ingredient was not an array or an object, rather an '${jsonElement::class.simpleName}'")
        }
        fun getItemStack(jsonObject: JsonObject, jsonContext: JsonContext, parseNbt: Boolean = true): ItemStack {
            val name = jsonContext.appendModId(JsonUtils.getString(jsonObject, "item"))
            val item = ForgeRegistries.ITEMS.getValue(ResourceLocation(name)) ?: throw JsonSyntaxException("Item '$name' does not match any known items")
            if (item == Items.AIR) l.warn("Item '$name' did not receive what was asked for, but instead got '${Items.AIR.registryName}': this may cause issues later on")
            if (item.hasSubtypes && !jsonObject.has("data")) throw JsonSyntaxException("Missing metadata for item '$name', but item specifies it requires it")

            if (!parseNbt) {
                return ItemStack(item, 1, JsonUtils.getInt(jsonObject, "data", 0))
            }

            if (!jsonObject.has("nbt")) {
                return ItemStack(item, JsonUtils.getInt(jsonObject, "count", 1), JsonUtils.getInt(jsonObject, "data", 0))
            }

            try {
                val nbt = jsonObject["nbt"]
                val compound = if (nbt.isJsonObject) JsonToNBT.getTagFromJson(Gson().toJson(nbt)) else JsonToNBT.getTagFromJson(JsonUtils.getString(nbt, "nbt"))
                val actualCompound = NBTTagCompound()
                if (compound.hasKey("ForgeCaps")) {
                    actualCompound.setTag("ForgeCaps", compound.getTag("ForgeCaps"))
                    compound.removeTag("ForgeCaps")
                }
                actualCompound.setTag("tag", compound)
                actualCompound.setString("id", name)
                actualCompound.setInteger("Count", JsonUtils.getInt(jsonObject, "count", 1))
                actualCompound.setInteger("Damage", JsonUtils.getInt(jsonObject, "data", 0))

                return ItemStack(actualCompound)
            } catch (e: NBTException) {
                e rethrowAs { m, c -> JsonSyntaxException(m, c) }
            }
        }

        private fun getIngredientFromArray(jsonArray: JsonArray, jsonContext: JsonContext): Ingredient {
            val ingredients = mutableListOf<Ingredient>()
            val vanilla = mutableListOf<Ingredient>()

            jsonArray.asSequence().map { getIngredient(it, jsonContext) }.forEach { (if (it::class == Ingredient::class) vanilla else ingredients) += it  }

            if (vanilla.isNotEmpty()) ingredients += Ingredient.merge(vanilla)

            return when (ingredients.count()) {
                0 -> throw JsonSyntaxException("No ingredients defined: must be at least 1")
                1 -> ingredients[0]
                else -> NowPublicCompoundIngredient(ingredients)
            }
        }

        private fun getIngredientFromObject(jsonObject: JsonObject, jsonContext: JsonContext) = this
                .getIngredientFactory(jsonContext.appendModId(JsonUtils.getString(jsonObject, "type", "minecraft:item")).toNameSpacedString())
                .parse(jsonContext, jsonObject)

        private fun getIngredientFactory(name: NameSpacedString) = gc
                ?.computeIfAbsent(nameToKey[INGREDIENTS] ?: error("Somebody messed with our internals! nameToKey[INGREDIENTS] == null")) { mutableMapOf() }
                ?.get(name) as? IIngredientFactory ?: error("The identifier '$name' does not identify any known ingredient type")

        private fun getCondition(name: NameSpacedString) = gc
                ?.computeIfAbsent(nameToKey[CONDITIONS] ?: error("Somebody messed with our internals! nameToKey[CONDITIONS] == null")) { mutableMapOf() }
                ?.get(name) as? IConditionFactory ?: error("The identifier '$name' does not identify any known condition type")
    }

    private val processFun by lazy {
        when (this.flags) {
            0 -> this::processDefaults
            1 -> this::processFactories
            2 -> this::processLateBoundFactories
            3 -> CatchingProcessor.throwException(IllegalArgumentException("Use ConstantsLoadingProcessor instead"))
            4 -> this::processRecipes
            else -> CatchingProcessor.throwException(IllegalArgumentException(this.flags.toString(radix = 2)))
        }
    }

    override fun process(content: JsonObject, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?) {
        gc = globalContext
        this.processFun(content, identifier, globalContext!!)
        gc = null
    }

    private fun processDefaults(content: JsonObject, identifier: NameSpacedString, globalContext: Context) =
            this.processJsonFactories(content, identifier, globalContext, false) { _, b ->
                b.toNameSpacedString()
            }

    private fun processFactories(content: JsonObject, identifier: NameSpacedString, globalContext: Context) =
            this.processJsonFactories(content, identifier, globalContext, true) { a, b ->
                val string = b.toNameSpacedString(defaultNamespace = a.nameSpace)
                if (string.nameSpace != a.nameSpace) {
                    l.warn("Attempting to register factory '${string.path}' with namespace '${string.nameSpace}' instead of the expected '${a.nameSpace}': this may cause errors later on!")
                }
                string
            }

    private fun processLateBoundFactories(content: JsonObject, identifier: NameSpacedString, globalContext: Context) {
        this.loadAssetsDataFactories(globalContext)
        try {
            this.processDefaults(content, identifier, globalContext)
        } catch (e: JsonSyntaxException) {
            if (!(e.message != null && e.message!!.contains("override"))) throw e
            l.info("Skipping registration of factory '$identifier' because it was already registered. Maybe another mod?")
        }
        l.debug("Dumping current status: ")
        l.debug("    CONDITIONS: ${globalContext[nameToKey[CONDITIONS] ?: error("")]}")
        l.debug("    RECIPES: ${globalContext[nameToKey[RECIPES] ?: error("")]}")
        l.debug("    INGREDIENTS: ${globalContext[nameToKey[INGREDIENTS] ?: error("")]}")
    }

    private fun processJsonFactories(content: JsonObject, identifier: NameSpacedString, globalContext: Context,
                                     allowOverrides: Boolean, namingFunction: (NameSpacedString, String) -> NameSpacedString) {
        content.entrySet().forEach {
            val name: String = it.key
            l.debug("Attempting to read data from JsonObject for factory type '$name'")
            val factoryClass = nameToInterface[name] ?: return@forEach l.warn("'$name' isn't a type of factory that is recognized by the system")
            val obj: JsonObject = it.value.asJsonObject
            obj.entrySet().forEach { factoryEntry ->
                val id = namingFunction(identifier, factoryEntry.key)
                if (!factoryEntry.value.isJsonPrimitive || !factoryEntry.value.asJsonPrimitive.isString) {
                    throw JsonSyntaxException("The entry for a factory must be composed of a string key and a string value, referring to the target class")
                }
                val factoryAny = Class.forName(factoryEntry.value.asString)!!.kotlin.createInstance()
                val factory = factoryClass.cast(factoryAny)
                val map: MutableMap<NameSpacedString, Any> = globalContext
                        .computeIfAbsent(nameToKey[name] ?: error("$name doesn't have a context key")) { mutableMapOf() }
                        .uncheckedCast()
                map[id]?.let {
                    l.bigWarn("""
                        An attempt of overriding a previously registered factory has been identified
                        ${if (allowOverrides) "Note that this attempt will not be blocked, but it may lead to errors in the future" else "This attempt will be blocked"}
                        Name of the conflict: $id
                    """.trimIndent(), L.DumpStackBehavior.DO_NOT_DUMP)
                    if (!allowOverrides) throw JsonSyntaxException("A non-allowed override has been identified for factory id $id")
                }
                map[id] = factory
            }
        }
    }

    private fun loadAssetsDataFactories(globalContext: Context) {
        l.info("Attempting to load factories from non-data-driven sources")
        with(CraftingHelper::class.java) {
            this@RecipeLoadingProcessor.loadFactory<IConditionFactory>(globalContext, CONDITIONS) {
                this.getDeclaredField("conditions").apply {
                    this.isAccessible = true
                }.get(null).uncheckedCast()
            }
            this@RecipeLoadingProcessor.loadFactory<IIngredientFactory>(globalContext, INGREDIENTS) {
                this.getDeclaredField("ingredients").apply {
                    this.isAccessible = true
                }.get(null).uncheckedCast()
            }
            this@RecipeLoadingProcessor.loadFactory<IRecipeFactory>(globalContext, RECIPES) {
                this.getDeclaredField("recipes").apply {
                    this.isAccessible = true
                }.get(null).uncheckedCast()
            }
        }
    }

    private fun <T : Any> loadFactory(globalContext: Context, name: String, supplier: () -> Map<ResourceLocation, T>) {
        val targetMap = globalContext[nameToKey[name] ?: error("Invalid name supplied: $name")] ?: error("Somebody is messing with our internals? Nice")
        val assetsMap = supplier()
        assetsMap.forEach { (k, v) ->
            val mappedClass = nameToInterface[name] ?: error("Invalid name supplied: $name")
            if (!mappedClass.isInstance(v)) error("${v::class.qualifiedName} does not match expected type ${mappedClass.qualifiedName}")
            val mappedInstance = mappedClass.cast(v)
            val identifier = NameSpacedString(k.namespace, k.path)
            if (targetMap[identifier] == null) {
                l.warn("A mod has registered the factory '$identifier' via code or assets. This will NOT survive an upgrade to 1.13! Start using data packs instead!")
                targetMap[identifier] = mappedInstance.uncheckedCast()
            }
        }
    }

    private fun processRecipes(content: JsonObject, identifier: NameSpacedString, globalContext: Context) {
        val jsonContext = globalContext.computeIfAbsent(contextMapKey) { mutableMapOf() }.computeIfAbsent(identifier.nameSpace) { JsonContext(identifier.nameSpace) }
        if (this.processRecipeConditions(content, jsonContext)) return
        this.getRecipe(content, globalContext, jsonContext)?.let {
            ForgeRegistries.RECIPES.register(it.setRegistryName(ResourceLocation(identifier.nameSpace, identifier.path)))
        }
    }

    private fun processRecipeConditions(content: JsonObject, jsonContext: JsonContext) =
            if (content.has(CONDITIONS)) this.processRecipeConditions(JsonUtils.getJsonArray(content, CONDITIONS), jsonContext) else false

    private fun processRecipeConditions(parent: JsonArray, jsonContext: JsonContext) =
            parent.asSequence().map { it.asJsonObject }.map { getCondition(it).parse(jsonContext, it) }.map { it.asBoolean }.any { !it }

    private fun getRecipe(content: JsonObject, globalContext: Context, jsonContext: JsonContext): IRecipe? {
        val type = jsonContext.appendModId(JsonUtils.getString(content, "type"))
        if (type.isBlank()) throw JsonSyntaxException("Type cannot be an empty string")
        val key = nameToKey[RECIPES] ?: error("Somebody messed with our internals! nameToKey[RECIPES] == null")
        return (globalContext.computeIfAbsent(key) { mutableMapOf() }[type.toNameSpacedString()] as IRecipeFactory).parse(jsonContext, content)
    }
}

internal class ConstantsPreprocessor : Preprocessor<String, Array<JsonObject>> {
    companion object {
        private val jsonReader: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create()
    }

    override fun preProcessData(content: String, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?): Array<JsonObject>? =
            try {
                jsonReader.fromJson(content, Array<JsonObject>::class.java)
            } catch (e: JsonSyntaxException) {
                throw JsonSyntaxException(
                        "The file identified by '$identifier' is not a valid JSON file. Please check your syntax.", e
                )
            }
}

internal class ConstantsLoadingProcessor : Processor<Array<JsonObject>> {
    override fun process(content: Array<JsonObject>, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?) {
        RecipeLoadingProcessor.l.warn(
                "Identified a file named '_constants.json' for namespace '${identifier.nameSpace}': this is deprecated and will not be present in 1.13. Please consider moving to tags instead"
        )
        val jsonContext = globalContext!!.computeIfAbsent(RecipeLoadingProcessor.contextMapKey) { mutableMapOf() }.computeIfAbsent(identifier.nameSpace) { JsonContext(identifier.nameSpace) }
        jsonContext::class.java.getDeclaredMethod("loadConstants", Array<JsonObject>::class.java).apply {
            this.isAccessible = true
            this.invoke(jsonContext, content)
        }
    }
}

private class NowPublicCompoundIngredient(list: List<Ingredient>) : CompoundIngredient(list)
