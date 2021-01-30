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

package net.thesilkminer.mc.boson.implementation.configuration

import net.minecraftforge.common.MinecraftForge
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.configuration.Configuration
import net.thesilkminer.mc.boson.api.configuration.ConfigurationRegistry
import net.thesilkminer.mc.boson.api.event.ConfigurationRegisterEvent
import net.thesilkminer.mc.boson.api.log.L

internal object ConfigurationManager : ConfigurationRegistry {
    private val l = L(MOD_NAME, "Configuration Manager")

    private val configurations = mutableMapOf<String, MutableList<Configuration>>()

    override fun registerConfiguration(configuration: Configuration) {
        val configurationRegistryName = configuration.toRegistryName()
        val modConfigurations = this.configurations.computeIfAbsent(configuration.owner) { mutableListOf() }
        check(modConfigurations.firstOrNull { it.toRegistryName() == configurationRegistryName } == null) {
            "The given configuration '$configurationRegistryName' was already registered"
        }
        modConfigurations.add(configuration)
        this.l.info("Successfully registered configuration $configurationRegistryName for Mod ID ${configuration.owner}")
    }

    internal fun gatherConfigurations() {
        this.l.info("Gathering configurations from mods")
        this.configurations.clear()
        MinecraftForge.EVENT_BUS.post(ConfigurationRegisterEvent(this))
        this.l.info("Successfully registered a total of ${this.configurations.values.flatten().count()} configurations")
    }

    internal fun getConfigurations(): Map<String, List<Configuration>> = this.configurations.toMap()
    internal fun getConfigurationsForMod(id: String) = this.getConfigurations()[id]

    private fun Configuration.toRegistryName() = "${this.owner}:${this.name}"
}
