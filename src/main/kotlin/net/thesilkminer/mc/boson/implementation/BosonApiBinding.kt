package net.thesilkminer.mc.boson.implementation

import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.thesilkminer.mc.boson.api.BosonApi
import net.thesilkminer.mc.boson.api.configuration.ConfigurationBuilder
import net.thesilkminer.mc.boson.api.configuration.ConfigurationFormat
import net.thesilkminer.mc.boson.api.distribution.Distribution
import net.thesilkminer.mc.boson.implementation.configuration.ForgeConfiguration
import java.nio.file.Path

class BosonApiBinding : BosonApi {
    override val configurationDirectory: Path
        get() = Loader.instance().configDir
                .toPath()
                .resolve("./BosonEnv/")
                .normalize()
                .toAbsolutePath()

    override fun buildConfiguration(builder: ConfigurationBuilder) = when (builder.type) {
        ConfigurationFormat.DEFAULT -> ForgeConfiguration(builder) // The default may vary between Major versions ONLY
        ConfigurationFormat.FORGE_CONFIG -> ForgeConfiguration(builder)
        else -> TODO("Not Yet Supported")
    }

    override val currentDistribution: Distribution get() = if (FMLCommonHandler.instance().side.isClient) Distribution.CLIENT else Distribution.DEDICATED_SERVER
}
