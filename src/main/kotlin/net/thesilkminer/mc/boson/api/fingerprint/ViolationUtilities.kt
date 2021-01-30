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

@file:JvmName("FU")

package net.thesilkminer.mc.boson.api.fingerprint

import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent
import net.thesilkminer.mc.boson.api.log.L
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
fun logViolationMessage(n: String, event: FMLFingerprintViolationEvent) {
    logViolationMessage(L(n, "Fingerprint Violation"), event)
}

@ApiStatus.Experimental
fun logViolationMessage(l: L, event: FMLFingerprintViolationEvent) {
    // TODO("Create wrapper in the API for FMLFingerprintViolationEvent")
    val logFun = if (event.isDirectory) l::bigWarn else l::bigError
    logFun("""
        AN INVALID FINGERPRINT HAS BEEN DETECTED
        
        The file '${event.source.name}' has been tampered with by a third party!
        The expected fingerprint signature was '${event.expectedFingerprint}', but instead the following were found:
        ${if (event.fingerprints.isEmpty()) "- NONE" else event.fingerprints.joinToString("\n - ", "- ")}
        This is **NOT SUPPORTED!**
        
        If you encounter crashes or other unexpected behavior: it's not OUR fault. Do NOT come to us!
    """.trimIndent(), L.DumpStackBehavior.FULL_DUMP)
}
