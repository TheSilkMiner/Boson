package net.thesilkminer.mc.boson.implementation

import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.thesilkminer.mc.boson.api.BosonApi
import net.thesilkminer.mc.boson.api.configuration.ConfigurationBuilder
import net.thesilkminer.mc.boson.api.configuration.ConfigurationFormat
import net.thesilkminer.mc.boson.api.distribution.Distribution
import net.thesilkminer.mc.boson.api.distribution.runSided
import net.thesilkminer.mc.boson.api.locale.Color
import net.thesilkminer.mc.boson.api.locale.Readability
import net.thesilkminer.mc.boson.api.locale.Style
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.implementation.configuration.ForgeConfiguration
import net.thesilkminer.mc.boson.implementation.configuration.JsonConfiguration
import java.nio.file.Path

class BosonApiBinding : BosonApi {
    private val l = L("Boson API", "BosonApiBinding")

    override val configurationDirectory: Path
        get() = Loader.instance().configDir
                .toPath()
                .resolve("./BosonEnv/")
                .normalize()
                .toAbsolutePath()

    override fun buildConfiguration(builder: ConfigurationBuilder) = when (builder.type) {
        ConfigurationFormat.DEFAULT -> ForgeConfiguration(builder) // The default may vary between Major versions ONLY
        ConfigurationFormat.FORGE_CONFIG -> ForgeConfiguration(builder)
        ConfigurationFormat.JSON -> JsonConfiguration(builder).apply { this.save() }
        else -> TODO("Not Yet Supported")
    }

    override val currentDistribution: Distribution get() = if (FMLCommonHandler.instance().side.isClient) Distribution.CLIENT else Distribution.DEDICATED_SERVER

    override fun localizeAndFormat(message: String, color: Color, style: Style, readability: Readability, vararg arguments: Any?): String =
            runSided(server = { { message } }, client = { { I18n.format(message, arguments).apply(color, style, readability) } })

    private fun String.apply(color: Color, style: Style, readability: Readability): String {
        fun Color.toTextFormatting() = when (this) {
            Color.DEFAULT -> null
            Color.AQUA -> TextFormatting.AQUA
            Color.BLACK -> TextFormatting.BLACK
            Color.BLUE -> TextFormatting.BLUE
            Color.DARK_AQUA -> TextFormatting.DARK_AQUA
            Color.DARK_BLUE -> TextFormatting.DARK_BLUE
            Color.DARK_GRAY -> TextFormatting.DARK_GRAY
            Color.DARK_GREEN -> TextFormatting.DARK_GREEN
            Color.DARK_PURPLE -> TextFormatting.DARK_PURPLE
            Color.DARK_RED -> TextFormatting.RED
            Color.GOLD -> TextFormatting.GOLD
            Color.GRAY -> TextFormatting.GRAY
            Color.GREEN -> TextFormatting.GREEN
            Color.PURPLE -> TextFormatting.LIGHT_PURPLE
            Color.RED -> TextFormatting.RED
            Color.WHITE -> TextFormatting.WHITE
            Color.YELLOW -> TextFormatting.YELLOW
        }
        fun Style.toTextFormatting() = when (this) {
            Style.NORMAL -> null
            Style.BOLD -> TextFormatting.BOLD
            Style.ITALIC -> TextFormatting.ITALIC
            Style.UNDERLINE -> TextFormatting.UNDERLINE
            Style.STRIKE_THROUGH -> TextFormatting.STRIKETHROUGH
        }
        fun Readability.toTextFormatting() = when (this) {
            Readability.READABLE -> null
            Readability.OBFUSCATED -> TextFormatting.OBFUSCATED
        }

        val platformColor = color.toTextFormatting()?.toString() ?: ""
        val platformStyle = style.toTextFormatting()?.toString() ?: ""
        val platformReadability = readability.toTextFormatting()?.toString() ?: ""

        if (platformStyle.isNotEmpty() && platformReadability.isNotEmpty()) {
            l.warn("Both style $style and readability $readability were specified for text '$this': this is not currently supported. Readability will take over")
        }

        val reset = if (platformColor.isNotEmpty() || platformReadability.isNotEmpty() || platformStyle.isNotEmpty()) TextFormatting.RESET.toString() else ""

        return "$platformColor${if (platformReadability.isNotEmpty()) platformReadability else platformStyle}$this$reset"
    }
}
