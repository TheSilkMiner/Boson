@file:JvmName("C")

package net.thesilkminer.mc.boson

import net.thesilkminer.mc.boson.api.modid.BOSON
import net.thesilkminer.mc.boson.api.modid.FERMION
import net.thesilkminer.mc.boson.api.modid.FORGELIN

internal const val MOD_ID = BOSON
internal const val MOD_NAME = "Boson"
internal const val MOD_VERSION = "@VERSION@"
internal const val MOD_MC_VERSION = "1.12.2"
internal const val MOD_CERTIFICATE_FINGERPRINT = "@FINGERPRINT@"

internal const val MOD_DEPENDENCIES = "required-after:forge@[14.23.5.2768,);" +
        "required-after:$FERMION@[1.0.1,);" +
        "required-after:$FORGELIN@[1.8.4,)"

@Suppress("SpellCheckingInspection")
internal const val KOTLIN_LANGUAGE_ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"
@Suppress("SpellCheckingInspection")
internal const val MOD_GUI_FACTORY = "net.thesilkminer.mc.boson.mod.client.configuration.BosonConfigurationGuiFactory"
