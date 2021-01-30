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

@file:JvmName("NSSU")

package net.thesilkminer.mc.boson.prefab.naming

import net.minecraft.util.ResourceLocation
import net.thesilkminer.mc.boson.api.id.NameSpacedString

fun NameSpacedString.toResourceLocation() = ResourceLocation(this.nameSpace, this.path)
fun ResourceLocation.toNameSpacedString() = NameSpacedString(this.namespace, this.path)

fun String.toNameSpacedString(defaultNamespace: String? = null) =
        if (this.indexOf(':') != -1) {
            val (namespace, path) = this.split(':', limit = 2)
            NameSpacedString(namespace, path)
        } else {
            NameSpacedString(defaultNamespace, this)
        }
