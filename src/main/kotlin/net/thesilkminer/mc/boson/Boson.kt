package net.thesilkminer.mc.boson

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLConstructionEvent
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.thesilkminer.mc.boson.api.event.BosonPreAvailableEvent

@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, dependencies = MOD_DEPENDENCIES,
        acceptedMinecraftVersions = MOD_MC_VERSION, certificateFingerprint = MOD_CERTIFICATE_FINGERPRINT,
        modLanguageAdapter = KOTLIN_LANGUAGE_ADAPTER, modLanguage = "kotlin")
object Boson {

    @Mod.EventHandler
    fun onConstruction(event: FMLConstructionEvent) {
        println("construction")
    }

    @Mod.EventHandler
    fun onPreInitialization(event: FMLPreInitializationEvent) {
        println("pre")
    }

    @Mod.EventHandler
    fun onInitialization(event: FMLInitializationEvent) {
        println("init")
    }

    @Mod.EventHandler
    fun onPostInitialization(event: FMLPostInitializationEvent) {
        println("post")
    }

    @Mod.EventHandler
    fun onLoadFinished(event: BosonPreAvailableEvent) {
        println("BOSON!")
    }

    @Mod.EventHandler
    fun onLoadComplete(event: FMLLoadCompleteEvent) {
        println("available")
    }

    @Mod.EventHandler
    fun onFingerprintViolation(event: FMLFingerprintViolationEvent) {
        println("violation")
    }
}
