@file:JvmName("DTKt")

package net.thesilkminer.mc.boson.implementation.communication

import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.communication.MessageHandler
import net.thesilkminer.mc.boson.api.communication.MessageHandlerRegistry
import net.thesilkminer.mc.boson.api.log.L
import java.util.concurrent.ConcurrentHashMap

object CommunicationManager : MessageHandlerRegistry {
    private val l = L(MOD_NAME, "Communication Manager")
    private val handlers = ConcurrentHashMap<String, MutableList<MessageHandler>>()

    override fun register(receiver: String, handler: MessageHandler) {
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
}
