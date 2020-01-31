package net.thesilkminer.mc.boson.prefab.communication

import net.thesilkminer.mc.boson.api.communication.Message
import kotlin.reflect.KClass

abstract class BasicMessage<T : Any>(final override val sender: String, final override val messageType: KClass<out T>, final override val key: String) : Message<T> {
    abstract override val content: T
}
