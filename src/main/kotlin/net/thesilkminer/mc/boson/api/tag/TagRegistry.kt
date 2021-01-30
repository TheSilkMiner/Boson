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

package net.thesilkminer.mc.boson.api.tag

import net.thesilkminer.mc.boson.api.id.NameSpacedString

interface TagRegistry {
    fun <T : Any> findAllTagsOf(type: TagType<T>): List<Tag<T>>
    fun <T : Any> findTag(type: TagType<T>, name: NameSpacedString): Tag<T>
    fun <T : Any> findFor(target: T, type: TagType<T>): List<Tag<T>>

    val isFrozen: Boolean

    operator fun <T : Any> get(type: TagType<T>) = this.findAllTagsOf(type)
    operator fun <T : Any> get(type: TagType<T>, name: NameSpacedString) = this.findTag(type, name)
    operator fun <T : Any> get(target: T, type: TagType<T>) = this.findFor(target, type)
}
