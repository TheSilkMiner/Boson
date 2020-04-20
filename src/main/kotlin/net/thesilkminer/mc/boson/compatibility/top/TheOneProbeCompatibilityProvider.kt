package net.thesilkminer.mc.boson.compatibility.top

import net.thesilkminer.mc.boson.api.modid.BOSON
import net.thesilkminer.mc.boson.api.modid.THE_ONE_PROBE
import net.thesilkminer.mc.boson.compatibility.BosonCompatibilityProvider
import net.thesilkminer.mc.boson.prefab.communication.FunctionMessage
import net.thesilkminer.mc.boson.prefab.compatibility.ModCompatibilityProvider

class TheOneProbeCompatibilityProvider : ModCompatibilityProvider(THE_ONE_PROBE), BosonCompatibilityProvider {
    override fun enqueueMessages() {
        FunctionMessage(sender = BOSON, key = "getTheOneProbe", content = TheOneProbePlugin::class).dispatchTo(THE_ONE_PROBE)
    }
}
