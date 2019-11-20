package net.thesilkminer.mc.boson.api.loader

import net.thesilkminer.mc.boson.api.id.NameSpacedString

interface Preprocessor<in T: Any, out R: Any> {
    fun preProcessData(content: T, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?): R
}
