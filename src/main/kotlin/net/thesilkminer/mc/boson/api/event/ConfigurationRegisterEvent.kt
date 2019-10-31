package net.thesilkminer.mc.boson.api.event

import net.minecraftforge.fml.common.eventhandler.Event
import net.thesilkminer.mc.boson.api.configuration.ConfigurationRegistry

class ConfigurationRegisterEvent(val configurationRegistry: ConfigurationRegistry) : Event()
