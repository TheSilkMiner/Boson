package net.thesilkminer.mc.boson.mod.common.communication

import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.api.communication.Message
import net.thesilkminer.mc.boson.api.communication.MessageHandler
import net.thesilkminer.mc.boson.api.log.L

internal class CommunicationMainHandler : MessageHandler {
    private val l = L(MOD_ID, "Message Handler")

    override fun handleMessage(message: Message<*>) {
        this.l.info("Received message $message")
    }
}
