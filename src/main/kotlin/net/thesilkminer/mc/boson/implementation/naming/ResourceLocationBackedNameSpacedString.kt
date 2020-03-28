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

package net.thesilkminer.mc.boson.implementation.naming

import net.minecraft.util.ResourceLocation
import net.thesilkminer.mc.boson.api.id.NameSpacedString

internal class ResourceLocationBackedNameSpacedString(domain: String?, path: String) : NameSpacedString {
    private val backend = ResourceLocation(domain ?: "", path)

    override val nameSpace: String = this.backend.namespace
    override val path: String = this.backend.path
    override fun compareTo(other: NameSpacedString) = this.backend.compareTo(ResourceLocation(other.nameSpace, other.path))
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is ResourceLocationBackedNameSpacedString -> this.backend == other.backend
        other is NameSpacedString -> this == ResourceLocationBackedNameSpacedString(other.nameSpace, other.path)
        else -> this.backend == other
    }
    override fun hashCode() = this.backend.hashCode()
    override fun toString() = this.backend.toString()
}
