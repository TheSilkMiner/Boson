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

@file:JvmName("TOPP")

package net.thesilkminer.mc.boson.compatibility.top

import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.IProbeInfoProvider
import mcjty.theoneprobe.api.ITheOneProbe
import mcjty.theoneprobe.api.NumberFormat
import mcjty.theoneprobe.api.ProbeMode
import mcjty.theoneprobe.api.TextStyleClass
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.prefab.energy.MEASUREMENT_UNIT
import net.thesilkminer.mc.boson.prefab.energy.energyHolder
import net.thesilkminer.mc.boson.prefab.energy.energyProducer
import net.thesilkminer.mc.boson.prefab.energy.hasEnergySupport
import net.thesilkminer.mc.boson.prefab.energy.isEnergyConsumer
import net.thesilkminer.mc.boson.prefab.energy.isEnergyHolder
import net.thesilkminer.mc.boson.prefab.energy.isEnergyProducer
import net.thesilkminer.mc.boson.prefab.energy.toUserFriendlyAmount
import java.util.function.Function

@Suppress("SpellCheckingInspection") private const val THE_ONE_PROBE_TOOLTIP_BEGIN = "top.tooltip.boson"

@Suppress("unused")
internal class TheOneProbePlugin : Function<ITheOneProbe?, Void?> {
    override fun apply(t: ITheOneProbe?): Void? = this(t).let { null }

    private operator fun invoke(top: ITheOneProbe?) {
        top?.registerProvider(EnergyInfoProvider())
    }
}

@Suppress("EXPERIMENTAL_API_USAGE")
private class EnergyInfoProvider : IProbeInfoProvider {
    private companion object {
        private const val ENERGY_TOOLTIP = "$THE_ONE_PROBE_TOOLTIP_BEGIN.energy"
    }

    override fun addProbeInfo(mode: ProbeMode?, probeInfo: IProbeInfo?, player: EntityPlayer?, world: World?, blockState: IBlockState?, data: IProbeHitData?) {
        if (mode == null || probeInfo == null || data == null || world == null) return
        val tileEntity = world.getTileEntity(data.pos) ?: return
        if (!tileEntity.hasEnergySupport()) return
        if (mode == ProbeMode.NORMAL) probeInfo.appendNormalEnergyInfo(tileEntity) else probeInfo.appendVerboseEnergyInfo(tileEntity)
    }

    override fun getID() = "$MOD_ID:energy"

    private fun IProbeInfo.appendNormalEnergyInfo(te: TileEntity) {
        if (te.isEnergyProducer()) this.appendVerboseProducerInfo(te)
        val energyHolder = te.energyHolder ?: return
        if (energyHolder.maximumCapacity.toLong() < 0) {
            // We're exceeding the normal Long size, so we can't use the progress
            // bar unless we started adding a lot of trickery, which is not what
            // we want: for these rare cases, we revert to the number system
            return this.appendVerboseHolderInfo(te)
        }

        this.progress(energyHolder.storedPower.toLong(), energyHolder.maximumCapacity.toLong(), this.energyProgressStyle())
    }

    private fun IProbeInfo.appendVerboseEnergyInfo(te: TileEntity) {
        this.appendVerboseProducerInfo(te)
        this.appendVerboseHolderInfo(te)
    }

    private fun IProbeInfo.appendVerboseProducerInfo(te: TileEntity) {
        if (te.isEnergyProducer()) {
            val power = te.energyProducer?.producedPower?.toUserFriendlyAmount(decimalDigits = 3) ?: throw IllegalStateException()
            @Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
            val rate = if (te.energyProducer?.productionRate != 1U) te.energyProducer?.productionRate ?: throw IllegalStateException() else null
            val rateText = if (rate == null) "" else "/${rate}t"
            this.horizontal()
                    .text("${TextStyleClass.PROGRESS}${IProbeInfo.STARTLOC}$ENERGY_TOOLTIP.producer.power${IProbeInfo.ENDLOC}: $power$rateText")
        }
    }

    private fun IProbeInfo.appendVerboseHolderInfo(te: TileEntity) {
        if (te.isEnergyHolder()) {
            val holder = te.energyHolder ?: throw IllegalStateException()
            val max = holder.maximumCapacity.toUserFriendlyAmount(decimalDigits = 3)
            val power = holder.storedPower.toUserFriendlyAmount(decimalDigits = 3)

            this.horizontal()
                    .text("${TextStyleClass.PROGRESS}${IProbeInfo.STARTLOC}$ENERGY_TOOLTIP.holder.status${IProbeInfo.ENDLOC}: $power / $max")
        }
    }

    private fun IProbeInfo.energyProgressStyle() =
            this.defaultProgressStyle()
                    .suffix(MEASUREMENT_UNIT)
                    .filledColor(0xFFDD00DD.toInt())
                    .alternateFilledColor(0xFF430043.toInt())
                    .borderColor(0xFF555555.toInt())
                    .numberFormat(NumberFormat.COMPACT)
}
