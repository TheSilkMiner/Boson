package net.thesilkminer.mc.boson.prefab.compatibility

import net.minecraftforge.fml.common.Loader
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityProvider

open class ModCompatibilityProvider(private val modId: String) : CompatibilityProvider {
    override fun canLoad() = Loader.isModLoaded(this.modId)
}
