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
            val tagsForState = bosonApi.tagRegistry[state, blockTagType]
            if (tagsForState.count() > 0) event.right += ""
            tagsForState.forEach { event.right += it.name.toString() }
        }
    }
}
