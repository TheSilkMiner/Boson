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

@file:JvmName("ProgressBarVisitorBuilder")

package net.thesilkminer.mc.boson.prefab.loader.progress

import net.minecraftforge.fml.common.ProgressManager
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.loader.LoadingPhase
import net.thesilkminer.mc.boson.api.loader.Location
import net.thesilkminer.mc.boson.api.loader.ProgressVisitor
import net.thesilkminer.mc.boson.api.log.L

class ProgressBarVisitor : ProgressVisitor {
    private val l = L(MOD_NAME, "Progress Bar Visitor")

    private var bar: ProgressManager.ProgressBar? = null
    private var nextPhase: String? = null

    override fun beginVisit() = Unit
    override fun visitPhases(total: Int) = this.l.debug("$total phases to go through")
    override fun visitPhase(phase: LoadingPhase<*>) {
        this.endVisit()
        this.l.debug("Preparing to push new bar for phase '${phase.name}'")
        this.nextPhase = phase.name
    }
    override fun visitItemsTotal(total: Int) {
        this.l.debug("Pushing bar")
        this.bar = ProgressManager.push(this.nextPhase, total)
        this.nextPhase = null
    }

    override fun visitLocation(location: Location, isDirectory: Boolean) {
        this.l.debug("Step for path ${location.friendlyName}")
        this.bar?.step(location.friendlyName)
    }

    override fun visitItems(amount: Int) = Unit
    override fun visitItem(name: NameSpacedString) = Unit
    override fun endVisit() = this.bar?.let { ProgressManager.pop(this.bar.apply { this@ProgressBarVisitor.l.debug("Popping bar '${this}'") }) } ?: Unit
}
