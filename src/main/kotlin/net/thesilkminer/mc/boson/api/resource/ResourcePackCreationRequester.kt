package net.thesilkminer.mc.boson.api.resource

import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
interface ResourcePackCreationRequester {
    fun apply(manager: ResourcePackCreationManager)
}
