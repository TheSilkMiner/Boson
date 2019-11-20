package net.thesilkminer.mc.boson.api.loader

import net.thesilkminer.mc.boson.api.id.NameSpacedString

interface IdentifierBuilder {
    fun makeIdentifier(location: Location, globalContext: Context?, phaseContext: Context?): NameSpacedString
}
