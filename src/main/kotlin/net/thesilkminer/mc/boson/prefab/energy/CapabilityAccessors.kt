@file:JvmName("CA")

package net.thesilkminer.mc.boson.prefab.energy

import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.thesilkminer.mc.boson.api.direction.Direction
import net.thesilkminer.mc.boson.prefab.direction.toFacing

fun ICapabilityProvider.hasEnergySupport(side: Direction? = null) = this.isEnergyConsumer(side) || this.isEnergyHolder(side) || this.isEnergyProducer(side)

fun ICapabilityProvider.isEnergyConsumer(side: Direction? = null) = this.hasCapability(consumerCapability, side?.toFacing())
fun ICapabilityProvider.isEnergyHolder(side: Direction? = null) = this.hasCapability(holderCapability, side?.toFacing())
fun ICapabilityProvider.isEnergyProducer(side: Direction? = null) = this.hasCapability(producerCapability, side?.toFacing())

fun ICapabilityProvider.getEnergyConsumer(side: Direction? = null) = this.getCapability(consumerCapability, side?.toFacing())
fun ICapabilityProvider.getEnergyHolder(side: Direction? = null) = this.getCapability(holderCapability, side?.toFacing())
fun ICapabilityProvider.getEnergyProducer(side: Direction? = null) = this.getCapability(producerCapability, side?.toFacing())

val ICapabilityProvider.energyConsumer get() = this.getEnergyConsumer()
val ICapabilityProvider.energyHolder get() = this.getEnergyHolder()
val ICapabilityProvider.energyProducer get() = this.getEnergyProducer()
