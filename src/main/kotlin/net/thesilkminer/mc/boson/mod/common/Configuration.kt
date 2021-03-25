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

@file:JvmName("CKt")

package net.thesilkminer.mc.boson.mod.common

import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.api.configuration.ConfigurationFormat
import net.thesilkminer.mc.boson.api.configuration.EntryType
import net.thesilkminer.mc.boson.api.configuration.configuration

internal val common = configuration {
    owner = MOD_ID
    name = "common"
    type = ConfigurationFormat.FORGE_CONFIG

    categories {
        "recipes" {
            comment = "Manages the Recipe Loading and Parsing feature"
            languageKey = "boson.configuration.common.recipes"

            entries {
                "suppress_update_warnings"(EntryType.BOOLEAN) {
                    comment = "Sets whether warnings regarding recipes and factories regarding 1.13 updates should be suppressed or not"
                    languageKey = "boson.configuration.common.recipes.suppress_update_warnings"
                    default = false

                    requiresGameRestart()
                }
            }
        }
    }
}
