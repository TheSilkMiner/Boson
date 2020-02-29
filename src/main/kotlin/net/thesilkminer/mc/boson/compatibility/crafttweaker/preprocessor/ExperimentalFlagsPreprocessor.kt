package net.thesilkminer.mc.boson.compatibility.crafttweaker.preprocessor

import crafttweaker.CraftTweakerAPI
import crafttweaker.preprocessor.PreprocessorActionBase
import crafttweaker.runtime.ScriptFile
import net.thesilkminer.mc.boson.compatibility.crafttweaker.CraftTweakerCompatibilityProvider

class ExperimentalFlagsPreprocessor(fileName: String, preprocessorLine: String, lineIndex: Int) : PreprocessorActionBase(fileName, preprocessorLine, lineIndex) {
    companion object {
        fun create(fileName: String, preprocessorLine: String, lineIndex: Int) = ExperimentalFlagsPreprocessor(fileName, preprocessorLine, lineIndex)
    }

    override fun executeActionOnFinish(scriptFile: ScriptFile?) {
        val experimentalFlags = this.parseExperimentalFlags()
        CraftTweakerAPI.logInfo("Script $scriptFile specifies experimental flags $experimentalFlags should be enabled: enabling")
        attachFlags(scriptFile, experimentalFlags)
    }

    override fun getPreprocessorName(): String = CraftTweakerCompatibilityProvider.EXPERIMENTAL_FLAGS_PREPROCESSOR

    private fun parseExperimentalFlags() =
            this.preprocessorLine
                    .removePrefix("#${this.preprocessorName} ")
                    .split(' ')
                    .asSequence()
                    .map { Pair(it, it.findFlag()) }
                    .onEach { if (it.second == null) CraftTweakerAPI.logError("Preprocessor flag '${it.first}' is not a valid experimental flag! Ignoring") }
                    .mapNotNull { it.second }
                    .onEach { if (it.isDeprecated) CraftTweakerAPI.logWarning("Preprocessor flag '${it.flagName}' is deprecated since behavior is non-experimental now: consider removing it") }
                    .filterNot { it.isDeprecated }
                    .toList()

    private fun String.findFlag() = ExperimentalFlag.values().find { it.flagName == this }
}
