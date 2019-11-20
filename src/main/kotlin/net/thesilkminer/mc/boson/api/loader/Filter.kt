package net.thesilkminer.mc.boson.api.loader

interface Filter {
    fun canLoad(location: Location): Boolean
}
