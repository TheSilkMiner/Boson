package net.thesilkminer.mc.boson.api.communication

import kotlin.reflect.KClass

interface Message<out T : Any> {
    val sender: String
    val messageType: KClass<out T>
    val key: String
    val content: T

    fun dispatchTo(receiver: String) = dispatchMessageTo(receiver, this)
}
