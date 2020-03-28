/*
 * Copyright (C) 2020  TheSilkMiner
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
