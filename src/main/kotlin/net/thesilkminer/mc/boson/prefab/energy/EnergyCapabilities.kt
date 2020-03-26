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
