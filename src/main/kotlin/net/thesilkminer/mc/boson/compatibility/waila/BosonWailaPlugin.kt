@file:JvmName("BWP")

package net.thesilkminer.mc.boson.compatibility.waila

import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import mcp.mobius.waila.api.IWailaDataProvider
import mcp.mobius.waila.api.IWailaPlugin
import mcp.mobius.waila.api.IWailaRegistrar
import mcp.mobius.waila.api.WailaPlugin
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.locale.toLocale
import net.thesilkminer.mc.boson.prefab.energy.energyHolder
import net.thesilkminer.mc.boson.prefab.energy.energyProducer
import net.thesilkminer.mc.boson.prefab.energy.hasEnergySupport
import net.thesilkminer.mc.boson.prefab.energy.isEnergyConsumer
import net.thesilkminer.mc.boson.prefab.energy.isEnergyHolder
import net.thesilkminer.mc.boson.prefab.energy.isEnergyProducer
import net.thesilkminer.mc.boson.prefab.energy.toUserFriendlyAmount

@Suppress("SpellCheckingInspection") private const val WAILA_TOOLTIP_BEGIN = "waila.tooltip.boson"
@Suppress("SpellCheckingInspection") private const val WAILA_CONFIG_BEGIN = "boson"

@Suppress("unused")
@WailaPlugin
class BosonWailaPlugin : IWailaPlugin {
    override fun register(registrar: IWailaRegistrar) {
        EnergyDataProvider().let {
            registrar.registerNBTProvider(it, Block::class.java)
            registrar.registerBodyProvider(it, Block::class.java)
        }

        registrar.addConfig(MOD_NAME, EnergyDataProvider.ENERGY_CONFIGURATION_VALUE, true)
    }
}

private class EnergyDataProvider : IWailaDataProvider {
    @Suppress("EXPERIMENTAL_API_USAGE")
    internal companion object {
        internal const val ENERGY_CONFIGURATION_VALUE = "$WAILA_CONFIG_BEGIN.energy"
        private const val ENERGY_TOOLTIP_DISCRIMINATOR = "energy"

        private const val ENERGY_COMPOUND_SPECIFIC_KEY = "boson:waila_transfer_energy_data_compound"
        private const val TILE_ENTITY_TYPE = "te_kind"
        private const val PRODUCER_POWER = "producer:power"
        private const val HOLDER_POWER = "holder:power"
        private const val HOLDER_CAP = "holder:max"

        private const val PRODUCER = 1
        private const val CONSUMER = 2
        private const val HOLDER = 4

        private fun TileEntity.toKind(): Int? {
            var value = 0
            if (this.isEnergyProducer()) value = value or PRODUCER
            if (this.isEnergyConsumer()) value = value or CONSUMER
            if (this.isEnergyHolder()) value = value or HOLDER
            return if (value == 0) null else value
        }

        private fun TileEntity.addProducerInfoTo(nbt: NBTTagCompound) {
            val producer = this.energyProducer ?: throw IllegalStateException()
            nbt.setLong(PRODUCER_POWER, producer.producedPower.toLong())
        }

        private fun TileEntity.addHolderInfoTo(nbt: NBTTagCompound) {
            val holder = this.energyHolder ?: throw IllegalStateException()
            nbt.setLong(HOLDER_POWER, holder.storedPower.toLong())
            nbt.setLong(HOLDER_CAP, holder.maximumCapacity.toLong())
        }

        @Suppress("unused", "unused_parameter") private fun TileEntity.addConsumerInfoTo(nbt: NBTTagCompound) = Unit // no info to add

        private fun MutableList<String>.appendProducerInfoFrom(nbt: NBTTagCompound) {
            this += "$WAILA_TOOLTIP_BEGIN.$ENERGY_TOOLTIP_DISCRIMINATOR.producer.power".toLocale(nbt.getLong(PRODUCER_POWER).toULong().toUserFriendlyAmount(decimalDigits = 3))
        }

        private fun MutableList<String>.appendHolderInfoFrom(nbt: NBTTagCompound) {
            this += "$WAILA_TOOLTIP_BEGIN.$ENERGY_TOOLTIP_DISCRIMINATOR.holder.power".toLocale(nbt.getLong(HOLDER_POWER).toULong().toUserFriendlyAmount(decimalDigits = 3))
            this += "$WAILA_TOOLTIP_BEGIN.$ENERGY_TOOLTIP_DISCRIMINATOR.holder.cap".toLocale(nbt.getLong(HOLDER_CAP).toULong().toUserFriendlyAmount(decimalDigits = 3))
        }

        @Suppress("unused", "unused_parameter") private fun MutableList<String>.appendConsumerInfoFrom(nbt: NBTTagCompound) = Unit
    }

    override fun getNBTData(player: EntityPlayerMP?, te: TileEntity?, tag: NBTTagCompound?, world: World?, pos: BlockPos?): NBTTagCompound {
        // Warning: this accounts for false and null
        if (te == null || !te.hasEnergySupport()) return super.getNBTData(player, te, tag, world, pos)

        val energyCompound = NBTTagCompound()
        val typeFlags = te.toKind() ?: return super.getNBTData(player, te, tag, world, pos)
        energyCompound.setInteger(TILE_ENTITY_TYPE, typeFlags)

        if (typeFlags and PRODUCER != 0) te.addProducerInfoTo(energyCompound)
        if (typeFlags and CONSUMER != 0) te.addConsumerInfoTo(energyCompound)
        if (typeFlags and HOLDER != 0) te.addHolderInfoTo(energyCompound)

        val actualTag = tag ?: NBTTagCompound()
        actualTag.setTag(ENERGY_COMPOUND_SPECIFIC_KEY, energyCompound)
        return actualTag
    }

    override fun getWailaBody(itemStack: ItemStack?, tooltip: MutableList<String>?, accessor: IWailaDataAccessor?, config: IWailaConfigHandler?): MutableList<String> {
        val actualTooltip = tooltip ?: mutableListOf()
        val nbtData = accessor?.nbtData ?: return actualTooltip
        if (!nbtData.hasKey(ENERGY_COMPOUND_SPECIFIC_KEY)) return actualTooltip
        if (config?.getConfig(ENERGY_CONFIGURATION_VALUE, true) != true) return actualTooltip

        val energyCompound = nbtData.getCompoundTag(ENERGY_COMPOUND_SPECIFIC_KEY)
        val bitFlags = energyCompound.getInteger(TILE_ENTITY_TYPE)

        if (bitFlags and PRODUCER != 0) actualTooltip.appendProducerInfoFrom(energyCompound)
        if (bitFlags and CONSUMER != 0) actualTooltip.appendConsumerInfoFrom(energyCompound)
        if (bitFlags and HOLDER != 0) actualTooltip.appendHolderInfoFrom(energyCompound)

        return actualTooltip
    }
}
