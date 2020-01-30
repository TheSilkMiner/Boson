package net.thesilkminer.mc.boson.api.event

import net.minecraftforge.fml.common.eventhandler.Event
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityProviderRegistry

class CompatibilityProviderRegistryEvent(val registry: CompatibilityProviderRegistry) : Event()
