/*
 * Copyright (C) 2020  TheSilkMiner
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

@file:JvmName("C")

package net.thesilkminer.mc.boson

import net.thesilkminer.mc.boson.api.modid.BOSON
import net.thesilkminer.mc.boson.api.modid.CRAFT_TWEAKER_2
import net.thesilkminer.mc.boson.api.modid.FERMION
import net.thesilkminer.mc.boson.api.modid.FORGE
import net.thesilkminer.mc.boson.api.modid.FORGELIN

internal const val MOD_ID = BOSON
internal const val MOD_NAME = "Boson"
internal const val MOD_VERSION = "@BOSON_VERSION@"
internal const val MOD_MC_VERSION = "1.12.2"
internal const val MOD_CERTIFICATE_FINGERPRINT = "@FINGERPRINT@"

internal const val MOD_DEPENDENCIES = "required-after:$FORGE@[14.23.5.2768,);" +
        "required-after:$FERMION@[1.0.2,);" +
        "required-after:$FORGELIN@[1.8.4,);" +
        "before:$CRAFT_TWEAKER_2"

@Suppress("SpellCheckingInspection")
internal const val KOTLIN_LANGUAGE_ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"
@Suppress("SpellCheckingInspection")
internal const val MOD_GUI_FACTORY = "net.thesilkminer.mc.boson.mod.client.configuration.BosonConfigurationGuiFactory"
