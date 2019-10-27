package net.thesilkminer.mc.boson

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLConstructionEvent
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.thesilkminer.mc.boson.api.event.BosonPreAvailableEvent
import net.thesilkminer.mc.boson.api.log.L

@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, dependencies = MOD_DEPENDENCIES,
        acceptedMinecraftVersions = MOD_MC_VERSION, certificateFingerprint = MOD_CERTIFICATE_FINGERPRINT,
        modLanguageAdapter = KOTLIN_LANGUAGE_ADAPTER, modLanguage = "kotlin")
object Boson {

    private val l = L(MOD_ID, "Main")

    @Mod.EventHandler
    fun onConstruction(event: FMLConstructionEvent) {
        this.l.info("construction")
    }

    @Mod.EventHandler
    fun onPreInitialization(event: FMLPreInitializationEvent) {
        this.l.info("pre")
    }

    @Mod.EventHandler
    fun onInitialization(event: FMLInitializationEvent) {
        this.l.info("init")
    }

    @Mod.EventHandler
    fun onPostInitialization(event: FMLPostInitializationEvent) {
        this.l.info("post")
    }

    @Mod.EventHandler
    fun onLoadFinished(event: BosonPreAvailableEvent) {
        this.l.info("BOSON!")
    }

    @Mod.EventHandler
    fun onLoadComplete(event: FMLLoadCompleteEvent) {
        this.l.info("available")
    }

    @Mod.EventHandler
    fun onFingerprintViolation(event: FMLFingerprintViolationEvent) {
        this.l.info("violation")
    }
}
