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
