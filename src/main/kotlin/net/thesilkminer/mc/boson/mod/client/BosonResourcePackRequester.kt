package net.thesilkminer.mc.boson.mod.client

import net.minecraftforge.fml.common.Loader
import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.api.modid.BASE
import net.thesilkminer.mc.boson.api.resource.ResourcePackCreationManager
import net.thesilkminer.mc.boson.api.resource.ResourcePackCreationRequester
import java.nio.file.Paths

class BosonResourcePackRequester : ResourcePackCreationRequester {
    override fun apply(manager: ResourcePackCreationManager) {
        if (!Loader.isModLoaded(BASE)) {
            manager.request(
                    owner = MOD_ID,
                    name = "User-added Resources",
                    description = "Resource pack that gets automatically injected for additional resources",
                    root = Paths.get(".").resolve("./resources")
            )
        }
    }
}
