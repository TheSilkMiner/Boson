package net.thesilkminer.mc.boson.api.communication

interface MessageHandlerRegistry {
    fun register(receiver: String, handler: MessageHandler)
    fun getHandlersFor(receiver: String): Sequence<MessageHandler>

    operator fun get(receiver: String) = this.getHandlersFor(receiver)
}
