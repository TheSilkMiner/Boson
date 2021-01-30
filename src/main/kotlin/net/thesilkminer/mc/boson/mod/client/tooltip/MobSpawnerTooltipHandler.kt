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

package net.thesilkminer.mc.boson.mod.client.tooltip

import net.minecraft.block.BlockMobSpawner
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.relauncher.Side
import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.locale.Color
import net.thesilkminer.mc.boson.api.locale.toLocale
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.prefab.naming.toNameSpacedString
import net.thesilkminer.mc.fermion.api.TransformingUtilities

@Mod.EventBusSubscriber(modid = MOD_ID, value = [Side.CLIENT])
@Suppress("SpellCheckingInspection")
object MobSpawnerTooltipHandler {
    private val l = L(MOD_NAME, "Mob Spawner Tooltip")
    private val isEnabled by lazy { TransformingUtilities.wasTransformed(BlockMobSpawner::class.java) }
    private val mobSpawnerItemBlock by lazy { Item.getItemFromBlock(Blocks.MOB_SPAWNER) }

    @JvmStatic
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    fun onItemTooltip(e: ItemTooltipEvent) {
        if (!this.isEnabled) {
            this.l.bigWarn("Unable to enable detailed mob spawner tooltip because mob spawner wasn't transformed! Disabling handler")
            MinecraftForge.EVENT_BUS.unregister(this::class.java)
            return
        }
        if (e.itemStack.item != mobSpawnerItemBlock) return

        val blockEntityTag = e.itemStack.tagCompound?.getCompound("BlockEntityTag")
        if (blockEntityTag == null) {
            e.toolTip += "boson.client.tooltip.spawn.none".toLocale(color = Color.DARK_GRAY)
            return
        }

        val spawnData = blockEntityTag.getCompound("SpawnData")
        if (spawnData == null || !spawnData.hasKey("id") || spawnData.getString("id").isEmpty()) {
            e.toolTip += "boson.client.tooltip.spawn.data_error".toLocale(color = Color.DARK_RED)
            return
        }

        val id = spawnData.getString("id")
        val targetEntry = ForgeRegistries.ENTITIES.entries.find { it.key.toNameSpacedString() == id.toNameSpacedString() }

        if (targetEntry == null) {
            e.toolTip += "boson.client.tooltip.spawn.entry_error".toLocale(id, color = Color.DARK_RED)
            return
        }

        e.toolTip += "boson.client.tooltip.spawn.target".toLocale(targetEntry.value.name, targetEntry.key.toString(), color = Color.DARK_AQUA)
    }

    private fun NBTTagCompound.getCompound(key: String) = if (this.hasKey(key)) this.getCompoundTag(key) else null
}
