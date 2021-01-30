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

@file:JvmName("EC")

package net.thesilkminer.mc.boson.prefab.energy

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.thesilkminer.mc.boson.api.energy.Consumer
import net.thesilkminer.mc.boson.api.energy.Holder
import net.thesilkminer.mc.boson.api.energy.Producer
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
@CapabilityInject(Consumer::class)
@Suppress("EXPERIMENTAL_API_USAGE")
lateinit var consumerCapability: Capability<Consumer>

@ApiStatus.Experimental
@CapabilityInject(Holder::class)
@Suppress("EXPERIMENTAL_API_USAGE")
lateinit var holderCapability: Capability<Holder>

@ApiStatus.Experimental
@CapabilityInject(Producer::class)
@Suppress("EXPERIMENTAL_API_USAGE")
lateinit var producerCapability: Capability<Producer>
