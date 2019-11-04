package net.thesilkminer.mc.boson

import net.thesilkminer.mc.boson.api.modid.FERMION
import net.thesilkminer.mc.boson.api.modid.FORGELIN

const val MOD_ID = "boson"
const val MOD_NAME = "Boson"
const val MOD_VERSION = "@VERSION@"
const val MOD_MC_VERSION = "1.12.2"
const val MOD_CERTIFICATE_FINGERPRINT = "@FINGERPRINT@"

const val MOD_DEPENDENCIES = "required-after:forge@[14.23.5.2768,);" +
        "required-after:$FERMION@[1.0.0,);" +
        "required-after:$FORGELIN@[1.8.4,)"

@Suppress("SpellCheckingInspection")
const val KOTLIN_LANGUAGE_ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"
@Suppress("SpellCheckingInspection")
const val MOD_GUI_FACTORY = "net.thesilkminer.mc.boson.mod.client.configuration.BosonConfigurationGuiFactory"
