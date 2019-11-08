package net.thesilkminer.mc.boson.mod.common

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.thesilkminer.mc.boson.MOD_ID
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.event.ConfigurationRegisterEvent
import net.thesilkminer.mc.boson.api.log.L

@Mod.EventBusSubscriber(modid = MOD_ID)
@Suppress("unused")
object RegistrationHandler {
    private val l = L(MOD_NAME, "Registration Handler")

    @JvmStatic
    @SubscribeEvent
    fun onConfigurationRegistration(event: ConfigurationRegisterEvent) {
        event.configurationRegistry.registerConfigurations(common, client)
    }
}
