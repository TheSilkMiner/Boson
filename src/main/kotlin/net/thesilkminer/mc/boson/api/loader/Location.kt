package net.thesilkminer.mc.boson.api.loader

import java.nio.file.Path

interface Location {
    val path: Path
    val friendlyName: String?
    val additionalContext: Context?
}
