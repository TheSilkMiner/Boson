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
