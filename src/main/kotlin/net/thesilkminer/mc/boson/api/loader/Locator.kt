package net.thesilkminer.mc.boson.api.loader

interface Locator {
    val locations: List<Lazy<Location>>

    fun clean() = Unit
}
