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

package net.thesilkminer.mc.boson.api.loader

import net.thesilkminer.mc.boson.api.id.NameSpacedString

interface ProgressVisitor {
    private class ChainingProgressVisitor(first: ProgressVisitor) : ProgressVisitor {
        private val visitors = mutableListOf<ProgressVisitor>()
        init { this.chain(first) }
        override fun beginVisit() = this.visitors.forEach { it.beginVisit() }
        override fun visitPhases(total: Int) = this.visitors.forEach { it.visitPhases(total) }
        override fun visitPhase(phase: LoadingPhase<*>) = this.visitors.forEach { it.visitPhase(phase) }
        override fun visitItemsTotal(total: Int) = this.visitors.forEach { it.visitItemsTotal(total) }
        override fun visitLocation(location: Location, isDirectory: Boolean) = this.visitors.forEach { it.visitLocation(location, isDirectory) }
        override fun visitItems(amount: Int) = this.visitors.forEach { it.visitItems(amount) }
        override fun visitItem(name: NameSpacedString) = this.visitors.forEach { it.visitItem(name) }
        override fun endVisit() = this.visitors.forEach { it.endVisit() }
        override fun chain(other: ProgressVisitor) = this.apply { this.visitors += other }
        override fun toString() = "${super.toString()}{chaining ${this.visitors}}"
    }

    fun beginVisit()
    fun visitPhases(total: Int)
    fun visitPhase(phase: LoadingPhase<*>)
    fun visitItemsTotal(total: Int)
    fun visitLocation(location: Location, isDirectory: Boolean)
    fun visitItems(amount: Int)
    fun visitItem(name: NameSpacedString)
    fun endVisit()

    fun chain(other: ProgressVisitor): ProgressVisitor = ChainingProgressVisitor(this).chain(other)
}
