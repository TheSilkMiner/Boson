@file:JvmName("FU")

package net.thesilkminer.mc.boson.api.fingerprint

import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent
import net.thesilkminer.mc.boson.api.log.L

fun logViolationMessage(n: String, event: FMLFingerprintViolationEvent) {
    logViolationMessage(L(n, "Fingerprint Violation"), event)
}

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
