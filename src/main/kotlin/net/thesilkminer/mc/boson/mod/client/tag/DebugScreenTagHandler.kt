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

package net.thesilkminer.mc.boson.mod.client.tag

import net.minecraft.client.Minecraft
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.prefab.tag.blockTagType
import net.thesilkminer.mc.boson.prefab.tag.has

@Mod.EventBusSubscriber(modid = MOD_ID, value = [Side.CLIENT])
@Suppress("unused")
object DebugScreenTagHandler {
    @JvmStatic
    @SubscribeEvent
    fun onDebugScreenRendering(event: RenderGameOverlayEvent.Text) {
        val mc = Minecraft.getMinecraft()
        val rayTrace = mc.objectMouseOver
        if (mc.gameSettings.showDebugInfo && !mc.isReducedDebug && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
            val state = mc.world.getBlockState(rayTrace.blockPos)
            val allTags = bosonApi.tagRegistry[blockTagType]
            val tagsForState = allTags.filter { it has state }
            if (tagsForState.count() > 0) event.right += ""
            tagsForState.forEach { event.right += "#${it.name}" }
        }
    }
}
