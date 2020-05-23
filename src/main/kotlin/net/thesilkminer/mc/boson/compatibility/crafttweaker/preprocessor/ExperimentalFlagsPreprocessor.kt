/*
 * Copyright (C) 2020  TheSilkMiner
 *
 * This file is part of Boson.
 *
 * Boson is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Boson is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Boson.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact information:
 * E-mail: thesilkminer <at> outlook <dot> com
 */

package net.thesilkminer.mc.boson.compatibility.crafttweaker.preprocessor

import crafttweaker.CraftTweakerAPI
import crafttweaker.preprocessor.PreprocessorActionBase
import crafttweaker.runtime.ScriptFile
import net.thesilkminer.mc.boson.compatibility.crafttweaker.CraftTweakerCompatibilityProvider

internal class ExperimentalFlagsPreprocessor(fileName: String, preprocessorLine: String, lineIndex: Int) : PreprocessorActionBase(fileName, preprocessorLine, lineIndex) {
    companion object {
        fun create(fileName: String, preprocessorLine: String, lineIndex: Int) = ExperimentalFlagsPreprocessor(fileName, preprocessorLine, lineIndex)
    }

    override fun executeActionOnFinish(scriptFile: ScriptFile?) {
        if (scriptFile?.name != fileName) return
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
