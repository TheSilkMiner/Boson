@file:JvmName("DTKt")

package net.thesilkminer.mc.boson.implementation.communication

import net.minecraftforge.common.MinecraftForge
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.communication.MessageHandler
import net.thesilkminer.mc.boson.api.communication.MessageHandlerRegistry
import net.thesilkminer.mc.boson.api.event.MessageHandlerRegisterEvent
import net.thesilkminer.mc.boson.api.log.L
import java.util.concurrent.ConcurrentHashMap

object CommunicationManager : MessageHandlerRegistry {
    private val l = L(MOD_NAME, "Communication Manager")
    private val handlers = ConcurrentHashMap<String, MutableList<MessageHandler>>()
    private var allowRegistration = false

    override fun register(receiver: String, handler: MessageHandler) {
        if (!allowRegistration) {
            this.l.warn("Unable to register handler '$handler' for receiver '$receiver': not in registry event!")
            return
        }
        this.handlers.computeIfAbsent(receiver) { mutableListOf() } += handler
        l.info("Successfully registered handler $handler for receiver '$receiver'")
    }

    override fun getHandlersFor(receiver: String): Sequence<MessageHandler> {
        return this.handlers[receiver].let {
            if (it == null) {
                l.warn("No handlers were registered for receiver '$receiver': any message sent won't be dispatched!")
                return@let sequenceOf()
            }
            it.asSequence()
        }
    }

    fun register() {
        this.l.info("Registering message handlers for communication")
        this.allowRegistration = true
        MinecraftForge.EVENT_BUS.post(MessageHandlerRegisterEvent(this))
        // Kinda reaching around, but it's the best solution
        bosonApi.compatibilityProviderRegistry.findAllProviders().forEach { it.registerMessageHandler(this) }
        this.allowRegistration = false
        this.l.info("Registration completed: starting dispatcher threads")
        Dispatcher.toString() // Force dispatcher to load
        this.l.info("Threads started: registration completed")
    }
}
