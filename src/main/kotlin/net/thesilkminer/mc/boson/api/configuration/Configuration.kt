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

package net.thesilkminer.mc.boson.api.configuration

import net.thesilkminer.mc.boson.api.distribution.Distribution
import java.nio.file.Path

interface Configuration {
    val format: ConfigurationFormat
    val targetDistribution: Distribution? //TODO("Make it count")
    val owner: String
    val name: String
    val location: Path
    val categories: List<Category>

    fun save()
    fun load()

    operator fun get(category: String, vararg subCategories: String): Category
}
