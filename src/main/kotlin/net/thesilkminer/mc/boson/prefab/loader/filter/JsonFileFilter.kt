package net.thesilkminer.mc.boson.prefab.loader.filter

import net.thesilkminer.mc.boson.api.loader.Filter
import net.thesilkminer.mc.boson.api.loader.Location

class JsonFileFilter : Filter {
    override fun canLoad(location: Location) = with (location.path.fileName) { this != null && this.toString().endsWith(".json") }
}
