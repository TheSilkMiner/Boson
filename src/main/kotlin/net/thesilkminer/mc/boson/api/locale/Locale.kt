/*
 * Copyright (C) 2021  TheSilkMiner
 *
 * This file is part of Boson.
 *
 * Boson is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Boson is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Boson.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact information:
 * E-mail: thesilkminer <at> outlook <dot> com
 */

@file:JvmName("Locale")

package net.thesilkminer.mc.boson.api.locale

import net.thesilkminer.mc.boson.api.bosonApi

fun String.toLocale(vararg arguments: Any?, color: Color = Color.DEFAULT, style: Style = Style.NORMAL, readability: Readability = Readability.READABLE) =
        bosonApi.localizeAndFormat(this, color, style, readability, *arguments)
