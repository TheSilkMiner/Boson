package net.thesilkminer.mc.boson

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLConstructionEvent
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityProvider
import net.thesilkminer.mc.boson.api.event.BosonPreAvailableEvent
import net.thesilkminer.mc.boson.api.fingerprint.logViolationMessage
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.compatibility.BosonCompatibilityProvider
import net.thesilkminer.mc.boson.implementation.communication.CommunicationManager
import net.thesilkminer.mc.boson.implementation.compatibility.CompatibilityProviderManager
import net.thesilkminer.mc.boson.implementation.configuration.ConfigurationManager
import net.thesilkminer.mc.boson.implementation.tag.BosonTagManager
import net.thesilkminer.mc.boson.mod.common.tag.initializeTagOreDictCompatibilityLayer
import net.thesilkminer.mc.boson.mod.common.tag.loadTags

@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, dependencies = MOD_DEPENDENCIES,
        acceptedMinecraftVersions = MOD_MC_VERSION, certificateFingerprint = MOD_CERTIFICATE_FINGERPRINT,
        guiFactory = MOD_GUI_FACTORY, modLanguageAdapter = KOTLIN_LANGUAGE_ADAPTER, modLanguage = "kotlin")
object Boson {
    private val l = L(MOD_NAME, "Main")

    @Mod.EventHandler
    fun onConstruction(event: FMLConstructionEvent) {
        this.l.info("Construction")
    }

    @Mod.EventHandler
    fun onPreInitialization(event: FMLPreInitializationEvent) {
        this.l.info("PreInitialization")
        CompatibilityProviderManager.registerProviders()
        ConfigurationManager.gatherConfigurations()
        CommunicationManager.register()
        CompatibilityProviderManager[BosonCompatibilityProvider::class].forEach { it.onPreInitialization() }
        BosonTagManager.fireTagTypeRegistrationEvent() // Moved to pre-init due to CraftTweaker
    }

    @Mod.EventHandler
    fun onInitialization(event: FMLInitializationEvent) {
        this.l.info("Initialization")
        loadTags()
        CompatibilityProviderManager.fire(CompatibilityProvider::enqueueMessages)
    }

    @Mod.EventHandler
    fun onPostInitialization(event: FMLPostInitializationEvent) {
        this.l.info("PostInitialization")
        CompatibilityProviderManager.fire(CompatibilityProvider::onPostInitialization)
    }

    @Mod.EventHandler
    fun onLoadFinished(event: BosonPreAvailableEvent) {
        this.l.info("BosonPreAvailable")
        initializeTagOreDictCompatibilityLayer()
        CompatibilityProviderManager.fire(CompatibilityProvider::onPreAvailable)
    }

    @Mod.EventHandler
    fun onLoadComplete(event: FMLLoadCompleteEvent) {
        this.l.info("LoadComplete (i.e. Available)")
    }

    @Mod.EventHandler
    fun onFingerprintViolation(event: FMLFingerprintViolationEvent) {
        logViolationMessage(MOD_NAME, event)
    }
}
