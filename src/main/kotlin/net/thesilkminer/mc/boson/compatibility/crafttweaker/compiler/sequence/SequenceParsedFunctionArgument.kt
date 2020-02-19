package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.sequence

import stanhebben.zenscript.definitions.ParsedFunctionArgument
import stanhebben.zenscript.type.ZenType

class SequenceParsedFunctionArgument(base: ParsedFunctionArgument, private val sequence: SequenceZenType) : ParsedFunctionArgument(base.name, sequence.genericType) {
    override fun getType(): ZenType = this.sequence.genericType
}
