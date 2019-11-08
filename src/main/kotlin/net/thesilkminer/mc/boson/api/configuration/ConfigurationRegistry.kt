package net.thesilkminer.mc.boson.api.configuration

interface ConfigurationRegistry {
    fun registerConfiguration(configuration: Configuration)
    fun registerConfigurations(vararg configurations: Configuration) = configurations.forEach(this::registerConfiguration)
}
