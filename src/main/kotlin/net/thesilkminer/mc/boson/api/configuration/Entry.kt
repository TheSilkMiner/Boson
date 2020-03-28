/*
 * Copyright (C) 2020  TheSilkMiner
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

package net.thesilkminer.mc.boson.api.configuration

interface Entry {
    class View(private val value: Any) {
        val boolean: Boolean get() = this.value as Boolean
        val string: String get() = this.value as String
        val int: Int get() = this.long.toInt()
        val short: Short get() = this.long.toShort()
        val byte: Byte get() = this.long.toByte()
        val long: Long get() = this.value as Long
        val double: Double get() = this.value as Double
        val float: Float get() = this.double.toFloat()
        val any: Any get() = this.value

        @Suppress("UNCHECKED_CAST")
        fun <T> asList() : List<T> = this.value as List<T>
    }

    val name: String
    val type: EntryType
    val comment: String
    val languageKey: String
    val default: Any
    val requiresMcRestart: Boolean
    val requiresWorldReload: Boolean
    val hasSlider: Boolean
    val bounds: Pair<Any?, Any?>

    var currentValue: Any

    operator fun invoke() = View(this.currentValue)
}
