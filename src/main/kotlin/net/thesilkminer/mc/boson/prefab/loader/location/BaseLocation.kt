package net.thesilkminer.mc.boson.prefab.loader.location

import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.Location
import java.nio.file.Path

data class BaseLocation(override val path: Path, override val friendlyName: String?, override val additionalContext: Context?) : Location
