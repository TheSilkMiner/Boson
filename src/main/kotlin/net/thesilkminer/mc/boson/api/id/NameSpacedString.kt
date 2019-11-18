package net.thesilkminer.mc.boson.api.id

import net.thesilkminer.mc.boson.api.bosonApi

interface NameSpacedString : Comparable<NameSpacedString> {
    companion object {
        operator fun invoke(nameSpace: String?, path: String) = bosonApi.constructNameSpacedString(nameSpace, path)
        operator fun invoke(path: String) = NameSpacedString(null, path)
    }

    val nameSpace: String
    val path: String
}
