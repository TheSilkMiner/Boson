/*
 * Copyright (C) 2021  TheSilkMiner
 *
 * This file is part of Boson.
 *
 * Boson is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Boson is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Boson.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact information:
 * E-mail: thesilkminer <at> outlook <dot> com
 */

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

internal object CommunicationManager : MessageHandlerRegistry {
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
        return this.handlers.computeIfAbsent(receiver) {
            l.warn("No handlers registered for receiver '$it': messages will not be dispatched!")
            mutableListOf()
        }.asSequence()
    }

    internal fun register() {
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
