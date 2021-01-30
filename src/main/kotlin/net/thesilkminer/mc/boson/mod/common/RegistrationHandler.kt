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

package net.thesilkminer.mc.boson.mod.common

import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.event.CompatibilityProviderRegistryEvent
import net.thesilkminer.mc.boson.api.event.ConfigurationRegisterEvent
import net.thesilkminer.mc.boson.api.event.MessageHandlerRegisterEvent
import net.thesilkminer.mc.boson.api.event.TagTypeRegisterEvent
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.compatibility.BosonCompatibilityProvider
import net.thesilkminer.mc.boson.mod.common.communication.CommunicationMainHandler
import net.thesilkminer.mc.boson.mod.common.recipe.loadDataPackRecipes
import net.thesilkminer.mc.boson.mod.common.tag.blocks
import net.thesilkminer.mc.boson.mod.common.tag.fluids
import net.thesilkminer.mc.boson.mod.common.tag.items

@Mod.EventBusSubscriber(modid = MOD_ID)
@Suppress("unused")
object RegistrationHandler {
    private val l = L(MOD_NAME, "Registration Handler")

    @JvmStatic
    @SubscribeEvent
    fun onConfigurationRegistration(event: ConfigurationRegisterEvent) {
        event.configurationRegistry.registerConfigurations(common)
    }

    @JvmStatic
    @SubscribeEvent
    fun onRecipeRegistry(event: RegistryEvent.Register<IRecipe>) {
        l.info("Received Register event for Recipes: starting loading of data-pack-based JSON recipes")
        loadDataPackRecipes()
    }

    @JvmStatic
    @SubscribeEvent
    fun onTagTypeRegistration(event: TagTypeRegisterEvent) {
        listOf(blocks, fluids, items).forEach { event.tagTypeRegistry.registerTagType(it) }
    }

    @JvmStatic
    @SubscribeEvent
    fun onCompatibilityProviderRegistration(event: CompatibilityProviderRegistryEvent) {
        event.registry.registerProvider(BosonCompatibilityProvider::class)
    }

    @JvmStatic
    @SubscribeEvent
    fun onMessageHandlerRegistration(event: MessageHandlerRegisterEvent) {
        event.registry.register(MOD_ID, CommunicationMainHandler())
    }
}
