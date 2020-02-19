package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.sequence

import crafttweaker.annotations.BracketHandler
import crafttweaker.annotations.ZenRegister
import crafttweaker.zenscript.IBracketHandler
import net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.sequence.SequencePartialExpression
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.parser.Token
import stanhebben.zenscript.symbols.IZenSymbol

@BracketHandler
@ZenRegister
class SequenceBracketHandler : IBracketHandler {
    override fun resolve(environment: IEnvironmentGlobal, tokens: List<Token>): IZenSymbol? {
        if (tokens.count() < 3) return null
        if (tokens[0].value != "sequence") return null
        val className = tokens.asSequence()
                .drop(2)
                .map(Token::getValue)
                .joinToString(separator = "")
        return IZenSymbol { SequencePartialExpression(it, className, environment) }
    }

    override fun getReturnedClass() = ZenSequence::class.java
    override fun getRegexMatchingString() = Regex("sequence:[\\w.]*").toString()
}
