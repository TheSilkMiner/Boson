package net.thesilkminer.mc.boson.compatibility.crafttweaker

import crafttweaker.CraftTweakerAPI
import crafttweaker.runtime.ScriptLoader
import crafttweaker.zenscript.GlobalRegistry
import net.thesilkminer.mc.boson.api.modid.CRAFT_TWEAKER_2
import net.thesilkminer.mc.boson.compatibility.BosonCompatibilityProvider
import net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.sequence.SequenceZenType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag.TagZenType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTag
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.sequence.ZenSequence
import net.thesilkminer.mc.boson.prefab.compatibility.ModCompatibilityProvider
import stanhebben.zenscript.type.ZenType

class CraftTweakerCompatibilityProvider : ModCompatibilityProvider(CRAFT_TWEAKER_2), BosonCompatibilityProvider {
    companion object {
        const val TAG_LOADER_NAME = "tags"
    }

    private var tagsLoader: ScriptLoader? = null

    override fun onPreInitialization() {
        CraftTweakerAPI.tweaker.registerLoadAbortedEvent(LoaderChangeListener::onLoaderAbortedEvent)
        CraftTweakerAPI.tweaker.registerLoadFinishedEvent(LoaderChangeListener::onLoaderFinishedEvent)
        CraftTweakerAPI.tweaker.registerLoadStartedEvent(LoaderChangeListener::onLoaderBeginEvent)
        GlobalRegistry.getTypes().typeMap.let {
            it[ZenSequence::class.java] = SequenceZenType(ZenType.ANY)
            it[ZenTag::class.java] = TagZenType(ZenType.ANY)
        }

        this.tagsLoader = CraftTweakerAPI.tweaker.getOrCreateLoader(TAG_LOADER_NAME).apply { this.mainName = TAG_LOADER_NAME }
    }

    override fun onPostInitialization() {
        CraftTweakerAPI.tweaker.loadScript(false, this.tagsLoader ?: throw IllegalStateException("Tag loader cannot be null"))
    }
}
