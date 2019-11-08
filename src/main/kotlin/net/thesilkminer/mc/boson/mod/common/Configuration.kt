@file:JvmName("CKt")

package net.thesilkminer.mc.boson.mod.common

import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.api.configuration.ConfigurationFormat
import net.thesilkminer.mc.boson.api.configuration.EntryType
import net.thesilkminer.mc.boson.api.configuration.configuration

val client = configuration {
    owner = MOD_ID
    name = "client"
    type = ConfigurationFormat.FORGE_CONFIG

    categories {
        "advanced_tooltips" {
            comment = "Manages the Advanced Tooltips feature"
            languageKey = "boson.configuration.client.advanced_tooltips"

            subCategories {
                "information" {
                    comment = "Manages the information that should be shown on the Tooltip"
                    languageKey = "boson.configuration.client.advanced_tooltips.information"

                    entries {
                        "ore_dictionary"(EntryType.BOOLEAN) {
                            comment = "Whether to show which OreDictionary entries the hovered item has"
                            languageKey = "boson.configuration.client.advanced_tooltips.information.ore_dictionary"
                            default = true
                        }
                    }
                }
            }

            entries {
                "enabled"(EntryType.BOOLEAN) {
                    comment = "Checks whether this feature should be enabled or not.\nNote that no other configuration option will work if this is set to false"
                    languageKey = "boson.configuration.client.advanced_tooltips.enabled"
                    default = true
                }
                "requires_vanilla_advanced_tooltips"(EntryType.BOOLEAN) {
                    comment = "Requires that advanced tooltips from vanilla are enabled in order for this feature to work.\nThis can be combined with 'requires_shift'"
                    languageKey = "boson.configuration.client.advanced_tooltips.requires_vanilla_advanced_tooltips"
                    default = true
                }
                "requires_shift"(EntryType.BOOLEAN) {
                    comment = "Requires that Shift is held to show detailed information about tooltips.\nThis can be combined with 'requires_vanilla_advanced_tooltips'"
                    languageKey = "boson.configuration.client.advanced_tooltips.requires_shift"
                    default = false
                }
            }
        }
    }
}

val common = configuration {
    owner = MOD_ID
    name = "common"
    type = ConfigurationFormat.FORGE_CONFIG
}
