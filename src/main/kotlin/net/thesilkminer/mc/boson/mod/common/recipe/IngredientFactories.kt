@file:Suppress("unused")

package net.thesilkminer.mc.boson.mod.common.recipe

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntComparators
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.client.util.RecipeItemHelper
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.JsonUtils
import net.minecraft.util.NonNullList
import net.minecraftforge.common.crafting.IIngredientFactory
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.oredict.OreDictionary
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.prefab.tag.isInTag
import net.thesilkminer.mc.boson.prefab.tag.itemTagType

class TagIngredientFactory : IIngredientFactory {
    private class TagIngredient(private val tag: Tag<ItemStack>) : Ingredient() {
        private val items = this.tag.elements

        private var ids = null as IntList?
        private var matchingStacks = null as Array<ItemStack>?
        private var lastSizeForIds = -1
        private var lastSizeForStacks = -1

        override fun invalidate() {
            this.ids = null
            this.matchingStacks = null
            super.invalidate()
        }

        override fun apply(p_apply_1_: ItemStack?): Boolean {
            if (p_apply_1_ == null) return false
            return p_apply_1_ isInTag this.tag
        }

        override fun getValidItemStacksPacked(): IntList {
            if (this.ids == null || this.lastSizeForIds != this.items.count()) {
                val idList = IntArrayList(this.items.count())
                this.items.asSequence()
                        .map {
                            if (it.metadata == OreDictionary.WILDCARD_VALUE) {
                                val list = NonNullList.create<ItemStack>()
                                it.item.getSubItems(CreativeTabs.SEARCH, list)
                                list.asSequence()
                            } else {
                                sequenceOf(it)
                            }
                        }
                        .flatten()
                        .map(RecipeItemHelper::pack)
                        .forEach { idList.add(it) }
                idList.sortWith(IntComparators.NATURAL_COMPARATOR)
                this.ids = idList
                this.lastSizeForIds = this.items.count()
            }
            return this.ids!!
        }

        override fun isSimple() = true

        override fun getMatchingStacks(): Array<ItemStack> {
            if (this.matchingStacks == null || this.lastSizeForStacks != this.items.count()) {
                val list = NonNullList.create<ItemStack>()
                this.items.forEach {
                    if (it.metadata == OreDictionary.WILDCARD_VALUE) {
                        it.item.getSubItems(CreativeTabs.SEARCH, list)
                    } else {
                        list += it
                    }
                }
                this.matchingStacks = list.toTypedArray()
                this.lastSizeForStacks = this.items.count()
            }
            return this.matchingStacks!!
        }
    }

    override fun parse(context: JsonContext, json: JsonObject): Ingredient {
        val tag = JsonUtils.getString(json, "tag")
        if (tag.first() != '#') throw JsonSyntaxException("Tag name is invalid: does not begin with #")
        if (tag.substring(startIndex = 1).isEmpty()) throw JsonSyntaxException("Expected a tag name, but found none")
        val tagName = tag.substring(startIndex = 1).split(':', limit = 2).let {
            if (it.count() == 1) NameSpacedString(it[0]) else NameSpacedString(it[0], it[1]) // ?
        }
        val target = bosonApi.tagRegistry[itemTagType, tagName]
        return TagIngredient(target)
    }
}
