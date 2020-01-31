package net.thesilkminer.mc.boson.api.communication

import net.thesilkminer.mc.boson.api.bosonApi

fun <T : Any> dispatchMessageTo(receiver: String, message: Message<T>): Unit = bosonApi.dispatchMessageTo(receiver, message)
