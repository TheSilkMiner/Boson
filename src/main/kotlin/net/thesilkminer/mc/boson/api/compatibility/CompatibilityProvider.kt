package net.thesilkminer.mc.boson.api.compatibility

import net.thesilkminer.mc.boson.api.communication.MessageHandlerRegistry

interface CompatibilityProvider {
    fun canLoad(): Boolean

    fun onPostInitialization() {}
    fun onPreAvailable() {}

    fun registerMessageHandler(registry: MessageHandlerRegistry)
    fun enqueueMessages() {}
}
