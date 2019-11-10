@file:JvmName("CKt")

package net.thesilkminer.mc.boson.mod.client.configuration

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
                        "tag"(EntryType.BOOLEAN) {
                            comment = "Whether to show which Boson Tags the hovered item has"
                            languageKey = "boson.configuration.client.advanced_tooltips.information.tag"
                            default = true
                        }
                        "nbt"(EntryType.BOOLEAN) {
                            comment = "Whether to show a JSON representation of the NBT of the item"
                            languageKey = "boson.configuration.client.advanced_tooltips.information.nbt"
                            default = true
                        }
                        "metadata"(EntryType.BOOLEAN) {
                            comment = "Whether to show a triple containing ID, metadata, and maximum metadata of an item"
                            languageKey = "boson.configuration.client.advanced_tooltips.information.metadata"
                            default = false
                        }
                        "language_key"(EntryType.BOOLEAN) {
                            comment = "Whether to show the language key of the item, used for translation"
                            languageKey = "boson.configuration.client.advanced_tooltips.information.language_key"
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
