package net.thesilkminer.mc.boson.api.loader

import net.thesilkminer.mc.boson.api.id.NameSpacedString

interface ProgressVisitor {
    private class ChainingProgressVisitor(private val a: ProgressVisitor, private val b: ProgressVisitor) : ProgressVisitor {
        override fun beginVisit() {
            this.a.beginVisit()
            this.b.beginVisit()
        }

        override fun visitPhases(total: Int) {
            this.a.visitPhases(total)
            this.b.visitPhases(total)
        }

        override fun visitPhase(phase: LoadingPhase<*>) {
            this.a.visitPhase(phase)
            this.b.visitPhase(phase)
        }

        override fun visitItemsTotal(total: Int) {
            this.a.visitItemsTotal(total)
            this.b.visitItemsTotal(total)
        }

        override fun visitLocation(location: Location, isDirectory: Boolean) {
            this.a.visitLocation(location, isDirectory)
            this.b.visitLocation(location, isDirectory)
        }

        override fun visitItems(amount: Int) {
            this.a.visitItems(amount)
            this.b.visitItems(amount)
        }

        override fun visitItem(name: NameSpacedString) {
            this.a.visitItem(name)
            this.b.visitItem(name)
        }

        override fun endVisit() {
            this.a.endVisit()
            this.b.endVisit()
        }
    }

    fun beginVisit()
    fun visitPhases(total: Int)
    fun visitPhase(phase: LoadingPhase<*>)
    fun visitItemsTotal(total: Int)
    fun visitLocation(location: Location, isDirectory: Boolean)
    fun visitItems(amount: Int)
    fun visitItem(name: NameSpacedString)
    fun endVisit()

    fun chain(other: ProgressVisitor): ProgressVisitor = ChainingProgressVisitor(this, other)
}
