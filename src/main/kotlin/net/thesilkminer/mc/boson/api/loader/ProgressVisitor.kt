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
