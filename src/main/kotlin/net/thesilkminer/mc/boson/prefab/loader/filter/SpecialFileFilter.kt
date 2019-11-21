package net.thesilkminer.mc.boson.prefab.loader.filter

import net.thesilkminer.mc.boson.api.loader.Filter
import net.thesilkminer.mc.boson.api.loader.Location
import java.nio.file.Path

class SpecialFileFilter(private val kind: Kind, private val inverted: Boolean = false) : Filter {

    enum class Kind(val matcher: (Path) -> Boolean) {
        FACTORIES({ it.fileName.toString().startsWith("_factories") }),
        JSON_SCHEMA({ it.fileName.toString() == "pattern.json" }),
        UNDERSCORE_PREFIX({ it.fileName.toString().startsWith("_") })
    }

    override fun canLoad(location: Location) = with (this.kind.matcher(location.path)) { if (this@SpecialFileFilter.inverted) !this else this }
}
