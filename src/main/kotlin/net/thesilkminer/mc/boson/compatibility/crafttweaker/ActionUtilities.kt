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

@file:JvmName("AC")

package net.thesilkminer.mc.boson.compatibility.crafttweaker

import crafttweaker.CraftTweakerAPI
import crafttweaker.IAction

private class Action(private val description: String, private val invalidDescription: String?, private val validator: (() -> Boolean)?, private val block: () -> Unit) : IAction {
    override fun describe() = this.description
    override fun apply() = this.block()
    override fun validate() = this.validator?.let { it() } ?: super.validate()
    override fun describeInvalid(): String = this.invalidDescription ?: super.describeInvalid()
}

internal fun ctAction(description: String, invalidDescription: String? = null, validator: (() -> Boolean)? = null, block: () -> Unit) =
        CraftTweakerAPI.apply(Action(description, invalidDescription, validator, block))
