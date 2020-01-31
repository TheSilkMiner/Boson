package net.thesilkminer.mc.boson.api.event

import net.minecraftforge.fml.common.eventhandler.Event
import net.thesilkminer.mc.boson.api.communication.MessageHandlerRegistry

class MessageHandlerRegisterEvent(val registry: MessageHandlerRegistry) : Event()
