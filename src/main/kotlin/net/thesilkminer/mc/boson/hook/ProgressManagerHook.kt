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

package net.thesilkminer.mc.boson.hook

import net.thesilkminer.mc.boson.api.log.L

@Suppress("unused")
object ProgressManagerHook {
    private const val PRE_INIT_MARKER = "\$Boson\$marker\$UsePreInit"
    private const val PRE_INIT_MESSAGE = "Initializing mods Phase 1"
    private val l = L("Boson ASM Hooks", "Progress Manager")

    @JvmStatic
    fun hookFmlProgressBarCreation(title: String, steps: Int): Int {
        val stack = Throwable().fillInStackTrace().stackTrace
        return if (stack[3].className == "net.minecraftforge.fml.common.Loader" && title == "Loading") {
            this.l.info("Found the ProgressBar creation for Loader: replacing steps")
            steps + 4 // Registry creation, registry firing (happens twice), post post initialization
        } else {
            steps
        }
    }

    @JvmStatic
    @Suppress("CascadeIf")
    fun checkForRegistryCreationMessage(message: String): String {
        return if (PRE_INIT_MESSAGE == message) "Creating Registries" else if (PRE_INIT_MARKER == message) PRE_INIT_MESSAGE else message
    }
}
