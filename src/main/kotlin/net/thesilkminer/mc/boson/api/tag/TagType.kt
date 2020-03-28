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

package net.thesilkminer.mc.boson.api.tag

import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import kotlin.reflect.KClass

interface TagType<T : Any> {
    companion object {
        operator fun <T : Any> invoke(type: KClass<out T>, directoryName: String, toElement: (NameSpacedString) -> T, equalityEvaluator: (T, T) -> Boolean) : TagType<T> =
                bosonApi.createTagType(type, directoryName, toElement, equalityEvaluator)
        operator fun <T : Any> invoke(type: KClass<out T>, directoryName: String, toElement: (NameSpacedString) -> T) : TagType<T> =
                this(type, directoryName, toElement) { a: T, b : T -> a == b }

        fun <T : Any> find(name: String) = bosonApi.findTagType<T>(name)
    }

    val type: KClass<out T>
    val directoryName: String
    val toElement: (NameSpacedString) -> T
    val equalityEvaluator: (T, T) -> Boolean
    val name: String get() = this.directoryName
}
