package net.thesilkminer.mc.boson.api.communication

interface MessageHandler {
    private class LambdaMessageHandler(private val handlerFun: (Message<*>) -> Unit) : MessageHandler {
        override fun handleMessage(message: Message<*>) = this.handlerFun(message)
    }

    companion object {
        fun from(function: (Message<*>) -> Unit): MessageHandler = LambdaMessageHandler(function)
    }

    fun handleMessage(message: Message<*>)
}
