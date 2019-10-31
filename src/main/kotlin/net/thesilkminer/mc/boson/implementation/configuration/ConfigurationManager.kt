package net.thesilkminer.mc.boson.implementation.configuration

import net.minecraftforge.common.MinecraftForge
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.configuration.Configuration
import net.thesilkminer.mc.boson.api.configuration.ConfigurationRegistry
import net.thesilkminer.mc.boson.api.event.ConfigurationRegisterEvent
import net.thesilkminer.mc.boson.api.log.L

object ConfigurationManager : ConfigurationRegistry {
    private val l = L(MOD_NAME, "Configuration Manager")

    private val configurations = mutableMapOf<String, MutableList<Configuration>>()

    override fun registerConfiguration(configuration: Configuration) {
        val configurationRegistryName = configuration.toRegistryName()
        val modConfigurations = this.configurations.computeIfAbsent(configuration.owner) { mutableListOf() }
        check(modConfigurations.asSequence().firstOrNull { it.toRegistryName() == configurationRegistryName } == null) {
            "The given configuration '$configurationRegistryName' was already registered"
        }
        modConfigurations.add(configuration)
        this.l.info("Successfully registered configuration $configurationRegistryName for Mod ID ${configuration.owner}")
    }

    fun gatherConfigurations() {
        this.l.info("Gathering configurations from mods")
        this.configurations.clear()
        MinecraftForge.EVENT_BUS.post(ConfigurationRegisterEvent(this))
        this.l.info("Successfully registered a total of ${this.configurations.count()} configurations")
    }

    fun getConfigurations(): Map<String, List<Configuration>> = this.configurations.toMap()
    fun getConfigurationsForMod(id: String) = this.getConfigurations()[id]

    private fun Configuration.toRegistryName() = "${this.owner}:${this.name}"
}
