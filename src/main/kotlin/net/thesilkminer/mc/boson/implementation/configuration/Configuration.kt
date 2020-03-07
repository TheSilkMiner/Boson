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
