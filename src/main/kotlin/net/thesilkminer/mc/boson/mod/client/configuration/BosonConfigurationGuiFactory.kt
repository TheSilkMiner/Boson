/*
 * Copyright (C) 2021  TheSilkMiner
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

package net.thesilkminer.mc.boson.mod.client.configuration

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.config.ConfigGuiType
import net.minecraftforge.fml.client.config.DummyConfigElement
import net.minecraftforge.fml.client.config.GuiConfig
import net.minecraftforge.fml.client.config.IConfigElement
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ModContainer
import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.configuration.Category
import net.thesilkminer.mc.boson.api.configuration.Configuration
import net.thesilkminer.mc.boson.api.configuration.Entry
import net.thesilkminer.mc.boson.api.configuration.EntryType
import net.thesilkminer.mc.boson.implementation.configuration.ConfigurationManager
import net.thesilkminer.mc.boson.implementation.configuration.toExtension

@Suppress("unused")
class BosonConfigurationGuiFactory(private val id: String, private val title: String) : IModGuiFactory {
    constructor() : this(MOD_ID, MOD_NAME)

    companion object {
        init {
            val hashBiMap = FMLClientHandler.instance().let {
                @Suppress("UNCHECKED_CAST")
                it::class.java.getDeclaredField("guiFactories").apply {
                    this.isAccessible = true
                }.get(it) as MutableMap<ModContainer, IModGuiFactory>
            }
            ConfigurationManager.getConfigurations().forEach { (k, _) ->
                val container = k.toModContainer()
                hashBiMap[container] = BosonConfigurationGuiFactory(container.modId, container.name)
            }
        }

        private fun String.toModContainer() = Loader.instance().activeModList.first { it.modId == this }!!
    }

    private lateinit var mc: Minecraft

    override fun hasConfigGui() = true
    override fun createConfigGui(parentScreen: GuiScreen?): GuiScreen =
            GuiConfig(parentScreen, (ConfigurationManager.getConfigurationsForMod(this.id) ?: listOf()).toConfigurationConfigElements(),
                    this.id, false, false, this.title)
    override fun runtimeGuiCategories(): MutableSet<IModGuiFactory.RuntimeOptionCategoryElement>? = null
    override fun initialize(minecraftInstance: Minecraft?) = with(this) { this.mc = minecraftInstance!! }

    private fun List<Configuration>.toConfigurationConfigElements(): List<IConfigElement> = this.map { it.toConfigElement() }
    private fun Configuration.toConfigElement() =
            DummyConfigElement.DummyCategoryElement(this.toButtonName(), "${this.owner}.configuration.${this.name}", this.categories.toFilteredConfigElements(this, null))
    private fun Configuration.toButtonName() = "${this.name}.${this.format.toExtension()}"
    private fun List<Category>.toFilteredConfigElements(parent: Configuration, pc: Category?) = this.toCategoryConfigElements(parent, pc) // TODO()
    private fun List<Category>.toCategoryConfigElements(parent: Configuration, pc: Category?): List<IConfigElement> = this.map { it.toConfigElement(parent, pc) }
    private fun Category.toConfigElement(parent: Configuration, parentCategory: Category?) =
            DummyConfigElement.DummyCategoryElement(
                    this.name,
                    if (this.languageKey.isBlank()) "${parent.owner}.configuration.${parentCategory?.name ?: "main"}.${this.name}" else this.languageKey,
                    this.categories.toCategoryConfigElements(parent, this).toMutableList().apply {
                        this.addAll(this@toConfigElement.entries.toEntryConfigElements(parent, this@toConfigElement))
                    }
            )
    private fun List<Entry>.toEntryConfigElements(parent: Configuration, pc: Category?): List<IConfigElement> = this.map { it.toConfigElement(parent, pc) }
    private fun Entry.toConfigElement(parent: Configuration, pc: Category?): IConfigElement = BosonConfigElement(this, parent, pc)
}

private class BosonConfigElement(private val entry: Entry, private val parent: Configuration, private val pc: Category?) : IConfigElement {
    override fun getConfigEntryClass() = null
    override fun getArrayEntryClass() = null
    override fun getComment() = this.entry.comment
    override fun getName() = this.entry.name
    override fun getChildElements() = mutableListOf<IConfigElement>()
    override fun getValidationPattern() = null // TODO("Maybe implement it for something?")
    override fun requiresMcRestart() = this.entry.requiresMcRestart
    override fun getMinValue() = this.entry.bounds.first
    override fun getLanguageKey() = if (this.entry.languageKey.isBlank()) "${this.parent.owner}.configuration.${this.pc?.name ?: "main"}.${this.entry.name}" else this.entry.languageKey
    override fun getQualifiedName() = this.name
    override fun isList() = this.entry.type.isList()
    override fun getDefault() = this.entry.default
    override fun isListLengthFixed() = false
    override fun get() = this.entry.currentValue
    override fun getDefaults() = (this.entry.default.toList() ?: listOf<Any?>()).toArray()
    override fun getMaxListLength() = -1
    override fun getMaxValue() = this.entry.bounds.second
    override fun setToDefault() = this.set(this.entry.default)
    override fun getType() = this.entry.type.toConfigType()
    override fun isProperty() = true
    override fun set(value: Any?) = this.entry.let { it.currentValue = value!! }.andSave()
    override fun set(aVal: Array<out Any>?) = this.entry.let { it.currentValue = aVal!! }.andSave()
    override fun getList() = (this.entry.currentValue.toList() ?: listOf<Any?>()).toTypedArray()
    override fun showInGui() = true
    override fun isDefault() = this.entry.currentValue == this.entry.default
    override fun getValidValues() = listOf<String>().toTypedArray()
    override fun requiresWorldRestart() = this.entry.requiresWorldReload

    private fun EntryType.isList() = this == EntryType.LIST_OF_BOOLEANS
            || this == EntryType.LIST_OF_WHOLE_NUMBERS
            || this == EntryType.LIST_OF_STRINGS
            || this == EntryType.LIST_OF_OBJECTS
            || this == EntryType.LIST_OF_REAL_NUMBERS
    private fun Any.toList() = if (this is List<*>) this else null
    private inline fun <reified T> List<T>.toArray() = this.toTypedArray()
    private fun EntryType.toConfigType() = when (this) {
        EntryType.STRING, EntryType.LIST_OF_STRINGS -> ConfigGuiType.STRING
        EntryType.REAL_NUMBER, EntryType.LIST_OF_REAL_NUMBERS -> ConfigGuiType.DOUBLE
        EntryType.WHOLE_NUMBER, EntryType.LIST_OF_WHOLE_NUMBERS -> ConfigGuiType.INTEGER
        EntryType.BOOLEAN, EntryType.LIST_OF_BOOLEANS -> ConfigGuiType.BOOLEAN
        EntryType.OBJECT, EntryType.LIST_OF_OBJECTS -> ConfigGuiType.STRING // Requires custom serialization techniques
    }
    @Suppress("unused") private fun Any.andSave() = this@BosonConfigElement.parent.save()
}
