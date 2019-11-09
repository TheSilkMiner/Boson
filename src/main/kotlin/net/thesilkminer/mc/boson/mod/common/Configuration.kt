@file:JvmName("CKt")

package net.thesilkminer.mc.boson.mod.common

import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.api.configuration.ConfigurationFormat
import net.thesilkminer.mc.boson.api.configuration.EntryType
import net.thesilkminer.mc.boson.api.configuration.configuration

val common = configuration {
    owner = MOD_ID
    name = "common"
    type = ConfigurationFormat.FORGE_CONFIG
}
