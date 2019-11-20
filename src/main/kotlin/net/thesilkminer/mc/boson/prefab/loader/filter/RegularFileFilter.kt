package net.thesilkminer.mc.boson.prefab.loader.filter

import net.thesilkminer.mc.boson.api.loader.Filter
import net.thesilkminer.mc.boson.api.loader.Location
import java.nio.file.Files

class RegularFileFilter : Filter {
    override fun canLoad(location: Location) = Files.isRegularFile(location.path)
}
