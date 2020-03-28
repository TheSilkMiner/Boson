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

@file:JvmName("3")

package net.thesilkminer.mc.boson.implementation.configuration

import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.configuration.ConfigurationBuilder
import net.thesilkminer.mc.boson.api.configuration.ConfigurationFormat
import java.lang.IllegalStateException
import java.nio.file.Path

internal fun ConfigurationBuilder.constructPath(format: ConfigurationFormat): Path = bosonApi.configurationDirectory
        .resolve("./${this.owner}/${this.name}.${format.toExtension()}")
        .normalize()
        .toAbsolutePath()

internal fun ConfigurationFormat.toExtension() = when (this) {
    ConfigurationFormat.DEFAULT -> throw IllegalStateException("The configuration cannot be in Default format at this stage")
    ConfigurationFormat.FORGE_CONFIG -> ForgeConfiguration.FORGE_CONFIGURATION_FILE_EXTENSION
    ConfigurationFormat.HOCON -> TODO("conf")
    ConfigurationFormat.JSON -> JsonConfiguration.JSON_CONFIGURATION_FILE_EXTENSION
    ConfigurationFormat.JSON5 -> TODO("json5 (maybe? the parser will decide)")
    ConfigurationFormat.TOML -> TODO("toml")
}
