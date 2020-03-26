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
