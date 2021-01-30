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

@file:JvmName("ECI")

package net.thesilkminer.mc.boson.mod.common

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.energy.Consumer
import net.thesilkminer.mc.boson.api.energy.Holder
import net.thesilkminer.mc.boson.api.energy.Producer
import net.thesilkminer.mc.boson.api.log.L

private val l = L(MOD_NAME, "Capabilities")

@Suppress("EXPERIMENTAL_API_USAGE")
fun injectCapabilities() {
    l.info("Injecting energy capabilities")
    CapabilityManager.INSTANCE.register(Consumer::class.java, CapabilityDummyStorage<Consumer>()) { fuckOff<Consumer>() }
    CapabilityManager.INSTANCE.register(Holder::class.java, CapabilityDummyStorage<Holder>()) { fuckOff<Holder>() }
    CapabilityManager.INSTANCE.register(Producer::class.java, CapabilityDummyStorage<Producer>())  { fuckOff<Producer>() }
    l.info("Injection completed")
}

private class CapabilityDummyStorage<T> : Capability.IStorage<T> {
    override fun readNBT(capability: Capability<T>?, instance: T, side: EnumFacing?, nbt: NBTBase?) = throw UnsupportedOperationException("IStorage is not supported")
    override fun writeNBT(capability: Capability<T>?, instance: T, side: EnumFacing?): NBTBase? = throw UnsupportedOperationException("IStorage is not supported")
}

private fun <T> fuckOff(): T = throw UnsupportedOperationException("Unable to build a default instance: no such instance exists!")
