package net.thesilkminer.mc.boson.mod.client.tooltip

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.oredict.OreDictionary
import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.locale.Color
import net.thesilkminer.mc.boson.api.locale.Style
import net.thesilkminer.mc.boson.api.locale.toLocale
import net.thesilkminer.mc.boson.api.tag.TagType
import net.thesilkminer.mc.boson.mod.client.configuration.client
import net.thesilkminer.mc.boson.prefab.tag.has
import org.lwjgl.input.Keyboard
import java.util.concurrent.TimeUnit

@Mod.EventBusSubscriber(modid = MOD_ID, value = [Side.CLIENT])
object AdvancedTooltipHandler {
    private const val REPLACE_WITH_NBT = "\$\$\$BOSON\$Replace\$NBT"

    private val isEnabled get() = client["advanced_tooltips"]["enabled"]().boolean
    private val mustBeAdvanced get() = client["advanced_tooltips"]["requires_vanilla_advanced_tooltips"]().boolean
    private val mustShift get() = client["advanced_tooltips"]["requires_shift"]().boolean

    private val tooltipLinesCache = CacheBuilder.newBuilder()
            .maximumSize(50L)
            .expireAfterAccess(3, TimeUnit.SECONDS)
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .build(object: CacheLoader<ItemStack, MutableList<String>>() {
                override fun load(key: ItemStack): MutableList<String> {
                    val list = mutableListOf<String>()
                    list += "boson.client.tooltip.advanced.begin".toLocale(color = Color.BLUE, style = Style.ITALIC)
                    if (client["advanced_tooltips", "information"]["metadata"]().boolean) this.addMetadata(key, list)
                    if (client["advanced_tooltips", "information"]["tag"]().boolean) this.addTags(key, list)
                    if (client["advanced_tooltips", "information"]["ore_dictionary"]().boolean) this.addOreDictionary(key, list)
                    if (client["advanced_tooltips", "information"]["language_key"]().boolean) this.addLanguageKey(key, list)
                    if (client["advanced_tooltips", "information"]["nbt"]().boolean) this.addNbt(key, list)
                    this.addRegistryName(key, list)
                    return list
                }

                private fun addOreDictionary(key: ItemStack, list: MutableList<String>) {
                    val oreDictionaryKeys = OreDictionary.getOreIDs(key).map { OreDictionary.getOreName(it) }
                    this.add(list, "boson.client.tooltip.advanced.ore_dictionary", oreDictionaryKeys)
                }

                private fun addTags(key: ItemStack, list: MutableList<String>) {
                    val itemTagType = TagType.find<ItemStack>("items")!!
                    val tagKeys = bosonApi.tagRegistry[itemTagType].filter { it has key }.map { it.name }.toList()
                    this.add(list, "boson.client.tooltip.advanced.tags", tagKeys)
                }

                private fun addNbt(key: ItemStack, list: MutableList<String>) {
                    this.add(list, "boson.client.tooltip.advanced.nbt", if (key.hasTagCompound()) listOf(REPLACE_WITH_NBT) else listOf())
                }

                private fun addMetadata(key: ItemStack, list: MutableList<String>) {
                    val id = Item.getIdFromItem(key.item)
                    val metadata = if (key.item.hasSubtypes) "${key.itemDamage}/${key.maxDamage}" else "boson.client.tooltip.advanced.metadata.none".toLocale()
                    val idString = "boson.client.tooltip.advanced.metadata.id".toLocale(id)
                    val metaString = "boson.client.tooltip.advanced.metadata.meta".toLocale(metadata)
                    this.add(list, "boson.client.tooltip.advanced.metadata", listOf(idString, metaString))
                }

                private fun addLanguageKey(key: ItemStack, list: MutableList<String>) {
                    this.add(list, "boson.client.tooltip.advanced.language_key", listOf(key.item.translationKey))
                }

                private fun addRegistryName(key: ItemStack, list: MutableList<String>) {
                    this.add(list, "boson.client.tooltip.advanced.registry_name", listOf(key.item.registryName!!))
                }

                private fun add(list: MutableList<String>, title: String, values: List<Any>) {
                    list += "  ${title.toLocale(color = Color.DARK_AQUA)}"
                    if (values.isEmpty()) list += "    ${"boson.client.tooltip.advanced.none".toLocale(color = Color.DARK_GRAY)}"
                    else values.forEach { list += "    ${TextFormatting.GRAY}- $it${TextFormatting.RESET}" }
                }
            })

    @JvmStatic
    @SubscribeEvent
    fun onItemTooltip(e: ItemTooltipEvent) {
        if (!this.isEnabled) return
        if (this.mustBeAdvanced && !e.flags.isAdvanced) return

        // Let's remove all instances of the registry name from the tooltip, so that we can place our own in the debug info section
        this.removeRegistryNameFromTooltipLines(e.toolTip, e.itemStack.item)

        // TODO("Modifier API")
        if (this.mustShift && !(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            e.toolTip += "boson.client.tooltip.advanced.shift".toLocale(color = Color.DARK_GRAY)
            return
        }
        e.toolTip += this.tooltipLinesCache[e.itemStack].apply { this.replaceNbtIfNeeded(e.itemStack) }
    }

    private fun removeRegistryNameFromTooltipLines(tooltip: MutableList<String>, item: Item) = tooltip.removeIf { it.contains(item.registryName!!.toString()) }

    private fun MutableList<String>.replaceNbtIfNeeded(key: ItemStack) {
        val shouldReplace = this.any { it.contains(REPLACE_WITH_NBT) }
        if (!shouldReplace) return
        val injectPosition = this.indexOf(this.find { it.contains(REPLACE_WITH_NBT) }!!)
        val injectList = mutableListOf<String>()
        val nbtCompound = key.tagCompound
        if (nbtCompound == null) {
            injectList += "boson.client.tooltip.advanced.nbt.null_error".toLocale(color = Color.DARK_RED)
        } else {
            nbtCompound.toTooltip(injectList)
        }
        injectList.reverse()
        injectList.forEach { this.add(injectPosition, "    ${TextFormatting.GRAY}$it${TextFormatting.RESET}") }
        this.remove(this.find { it.contains(REPLACE_WITH_NBT) }!!)
    }

    private fun NBTTagCompound.toTooltip(list: MutableList<String>, pad: String = "", level: Int = 0, name: String? = null) {
        list += "$pad${if (name == null) "" else "$name: "}{"
        this.keySet.forEachIndexed { i, it -> this.getTag(it).toTooltip(list, "$pad  ", if (i == this.keySet.size - 1) 0 else level + 1, it) }
        list += "$pad}${if (level > 0) "," else ""}"
    }

    private fun NBTTagList.toTooltip(list: MutableList<String>, pad: String, level: Int, name: String?) {
        list += "$pad${if (name == null) "" else "$name: "}["
        (0 until this.tagCount()).forEach { this[it].toTooltip(list, "$pad  ", if (it == this.tagCount() - 1) 0 else level + 1, null) }
        list += "$pad]${if (level > 0) "," else ""}"
    }

    private fun NBTBase.toTooltip(list: MutableList<String>, pad: String, level: Int, name: String?) {
        if (this is NBTTagCompound) return this.toTooltip(list, pad, level, name)
        if (this is NBTTagList) return this.toTooltip(list, pad, level, name)
        list += "$pad${if (name == null) "" else "$name: "}$this${if (level > 0) "," else ""}"
    }
}
