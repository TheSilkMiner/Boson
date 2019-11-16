@file:JvmName("3")

package net.thesilkminer.mc.boson.implementation.configuration

import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.configuration.ConfigurationBuilder
import net.thesilkminer.mc.boson.api.configuration.ConfigurationFormat
import java.nio.file.Path

fun ConfigurationBuilder.constructPath(format: ConfigurationFormat): Path = bosonApi.configurationDirectory
        .resolve("./${this.owner}/${this.name}.${format.toExtension()}")
        .normalize()
        .toAbsolutePath()

private fun ConfigurationFormat.toExtension() = when (this) {
    ConfigurationFormat.DEFAULT -> TODO()
    ConfigurationFormat.FORGE_CONFIG -> ForgeConfiguration.FORGE_CONFIGURATION_FILE_EXTENSION
    ConfigurationFormat.HOCON -> TODO()
    ConfigurationFormat.JSON -> JsonConfiguration.JSON_CONFIGURATION_FILE_EXTENSION
    ConfigurationFormat.JSON5 -> TODO()
    ConfigurationFormat.TOML -> TODO()
}
