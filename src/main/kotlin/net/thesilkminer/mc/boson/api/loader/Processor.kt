package net.thesilkminer.mc.boson.api.loader

import net.thesilkminer.mc.boson.api.id.NameSpacedString

interface Processor<in T: Any> {
    fun process(content: T, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?)
}
