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

package net.thesilkminer.mc.boson.prefab.loader.progress

import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ModContainer
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.loader.LoadingPhase
import net.thesilkminer.mc.boson.api.loader.Location
import net.thesilkminer.mc.boson.api.loader.ProgressVisitor

class ActiveModContainerVisitor : ProgressVisitor {
    private var activeModContainer: ModContainer? = null

    override fun beginVisit() = this.let { this.activeModContainer = Loader.instance().activeModContainer() }
    override fun visitPhases(total: Int) = Unit
    override fun visitPhase(phase: LoadingPhase<*>) = Unit
    override fun visitItemsTotal(total: Int) = Unit
    override fun visitLocation(location: Location, isDirectory: Boolean) = Unit
    override fun visitItems(amount: Int) = Unit
    override fun visitItem(name: NameSpacedString) = Loader.instance().setActiveModContainer(Loader.instance().modList.find { it.modId == name.nameSpace } ?: this.activeModContainer)
    override fun endVisit() = Loader.instance().setActiveModContainer(this.activeModContainer)
}
