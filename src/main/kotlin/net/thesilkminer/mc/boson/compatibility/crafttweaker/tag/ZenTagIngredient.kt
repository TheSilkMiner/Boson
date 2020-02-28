package net.thesilkminer.mc.boson.compatibility.crafttweaker.tag

import crafttweaker.CraftTweakerAPI
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IIngredient
import crafttweaker.api.item.IItemCondition
import crafttweaker.api.item.IItemStack
import crafttweaker.api.item.IItemTransformer
import crafttweaker.api.item.IItemTransformerNew
import crafttweaker.api.item.IngredientOr
import crafttweaker.api.item.IngredientStack
import crafttweaker.api.liquid.ILiquidStack
import crafttweaker.api.oredict.IOreDictEntry
import crafttweaker.api.player.IPlayer
import net.minecraftforge.oredict.OreDictionary
import net.thesilkminer.mc.boson.compatibility.crafttweaker.naming.ZenNameSpacedString
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toNativeStack
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toZen
import net.thesilkminer.mc.boson.mod.common.recipe.TagIngredient
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenGetter

@ZenClass("net.thesilkminer.mc.boson.zen.tag.TagIngredient")
@ZenRegister
class ZenTagIngredient(val tagIngredient: TagIngredient, private val mark: String? = null, private val conditions: List<IItemCondition> = listOf(),
                       private val newTransformers: List<IItemTransformerNew> = listOf(), private val transformers: List<IItemTransformer> = listOf()) : IIngredient, IOreDictEntry {

    val tagName: ZenNameSpacedString @ZenGetter(value = "tagName") get() = this.tagIngredient.tag.name.toZen()

    override fun getItems(): MutableList<IItemStack> = this.tagIngredient.matchingStacks.map { it.toZen(wildcard = it.metadata == OreDictionary.WILDCARD_VALUE)!! }.toMutableList()
    override fun contains(ingredient: IIngredient?) = ingredient?.items?.all(this::matches) ?: false
    override fun getLiquids(): MutableList<ILiquidStack> = mutableListOf()
    override fun toCommandString(): String = this.tagIngredient.tag.name.let { "<tag-item:${it.nameSpace}:${it.path}>" }
    override fun applyNewTransform(item: IItemStack?): IItemStack? = item
    override fun amount(amount: Int): IIngredient = IngredientStack(this, amount)
    override fun transformNew(transformer: IItemTransformerNew?): IIngredient = if (transformer != null) this.copy(newTransformers = this.newTransformers.append(transformer)) else this
    override fun applyTransform(item: IItemStack?, byPlayer: IPlayer?): IItemStack? = item.applyTransforms(byPlayer, this.transformers)
    override fun getInternal(): Any = this.tagIngredient
    override fun matchesExact(item: IItemStack?): Boolean = this.contains(item) && this.conditions.all { it.matches(item) }
    override fun getAmount(): Int = 1
    override fun marked(mark: String?): IIngredient = this.copy(mark = mark)
    override fun hasTransformers(): Boolean = this.transformers.any()
    override fun getMark(): String? = this.mark
    override fun getItemArray(): Array<IItemStack> = this.items.toTypedArray()
    override fun hasNewTransformers(): Boolean = this.newTransformers.any()
    override fun only(condition: IItemCondition?): IIngredient = if (condition != null) this.copy(conditions = this.conditions.append(condition)) else this
    override fun transform(transformer: IItemTransformer?): IIngredient = if (transformer != null) this.copy(transformers = this.transformers.append(transformer)) else this
    override fun or(ingredient: IIngredient?): IIngredient = IngredientOr(this, ingredient)
    override fun matches(item: IItemStack?): Boolean = this.contains(item) && this.conditions.all { it.matches(item) }
    override fun matches(liquid: ILiquidStack?): Boolean = false

    override fun contains(item: IItemStack?): Boolean = this.tagIngredient.apply(item.toNativeStack())
    override fun addAll(entry: IOreDictEntry?) = CraftTweakerAPI.logError("Unable to manipulate a tag when not in the 'tags' loader")
    override fun getName(): String = this.tagIngredient.tag.name.toString()
    override fun getFirstItem(): IItemStack? = this.tagIngredient.tag.elements.firstOrNull()?.let { it.toZen(wildcard = it.metadata == OreDictionary.WILDCARD_VALUE) }
    override fun mirror(other: IOreDictEntry?) = CraftTweakerAPI.logError("Mirroring is not supported by tags")
    override fun add(vararg items: IItemStack?) = CraftTweakerAPI.logError("Unable to manipulate a tag when not in the 'tags' loader")
    override fun isEmpty(): Boolean = this.tagIngredient.tag.elements.isEmpty()
    override fun remove(vararg items: IItemStack?) = CraftTweakerAPI.logError("Unable to manipulate a tag when not in the 'tags' loader")
    override fun removeItems(items: Array<out IItemStack>?) = CraftTweakerAPI.logError("Unable to manipulate a tag when not in the 'tags' loader")
    override fun addItems(items: Array<out IItemStack>?) = CraftTweakerAPI.logError("Unable to manipulate a tag when not in the 'tags' loader")

    private fun copy(mark: String? = this.mark, conditions: List<IItemCondition> = this.conditions, newTransformers: List<IItemTransformerNew> = this.newTransformers,
                     transformers: List<IItemTransformer> = this.transformers) = ZenTagIngredient(this.tagIngredient, mark, conditions, newTransformers, transformers)

    private tailrec fun IItemStack?.applyTransforms(byPlayer: IPlayer?, transformers: List<IItemTransformer>): IItemStack? {
        if (this == null) return null
        if (transformers.count() <= 0) return this
        return transformers[0].transform(this, byPlayer).applyTransforms(byPlayer, transformers.drop(1))
    }

    private fun <T> List<T>.append(element: T) = this.toMutableList().apply { this.add(element) }.toList()
}
