@file:JvmName("Locale")

package net.thesilkminer.mc.boson.api.locale

import net.thesilkminer.mc.boson.api.bosonApi

fun String.toLocale(vararg arguments: Any?, color: Color = Color.DEFAULT, style: Style = Style.NORMAL, readability: Readability = Readability.READABLE) =
        bosonApi.localizeAndFormat(this, color, style, readability, *arguments)
