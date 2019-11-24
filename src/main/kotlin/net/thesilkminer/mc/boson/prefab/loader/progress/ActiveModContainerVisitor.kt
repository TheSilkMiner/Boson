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
