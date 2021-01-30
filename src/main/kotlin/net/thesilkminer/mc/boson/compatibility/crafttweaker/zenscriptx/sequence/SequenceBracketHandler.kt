/*
 * Copyright (C) 2021  TheSilkMiner
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
