package net.thesilkminer.mc.boson.api.event

import net.minecraftforge.fml.common.eventhandler.Event
import net.thesilkminer.mc.boson.api.tag.TagTypeRegistry

class TagTypeRegisterEvent(val tagTypeRegistry: TagTypeRegistry) : Event()
