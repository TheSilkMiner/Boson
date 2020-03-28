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

package net.thesilkminer.mc.boson.compatibility.crafttweaker.tag

import crafttweaker.annotations.BracketHandler
import crafttweaker.annotations.ZenRegister
import crafttweaker.zenscript.IBracketHandler
import net.thesilkminer.mc.boson.compatibility.crafttweaker.CraftTweakerCompatibilityProvider
import net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag.TagCreationExpression
import net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag.TagIngredientCreationExpression
import net.thesilkminer.mc.boson.compatibility.crafttweaker.currentLoaderName
import net.thesilkminer.mc.boson.prefab.tag.itemTagType
import stanhebben.zenscript.ZenTokener
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.parser.Token
import stanhebben.zenscript.symbols.IZenSymbol

@BracketHandler(priority = 100)
@ZenRegister
class TagBracketHandler : IBracketHandler {
    override fun resolve(environment: IEnvironmentGlobal?, tokens: List<Token>?): IZenSymbol? {
        if (tokens == null || !tokens.isStructureValid()) {
            environment?.warning("Invalid tag bracket handler '${tokens?.join()}': did you mean something else?")
            return null
        }

        if (currentLoaderName == "preinit") {
            environment?.error("Unable to use tag bracket handler '${tokens.join()}' when in 'preinit' loader")
            return null
        }

        val tagType = tokens[2].value
        val tagName = tokens.asSequence()
                .drop(4)
                .map(Token::getValue)
                .joinToString(separator = "")

        if (currentLoaderName != CraftTweakerCompatibilityProvider.TAG_LOADER_NAME && tagType != itemTagType.name) {
            environment?.error("Unable to use '${tagType}' tag bracket handler '${tokens.join()}' when not in 'tags' loader")
            return null
        }

        val constructor = if (currentLoaderName != CraftTweakerCompatibilityProvider.TAG_LOADER_NAME) ::TagIngredientCreationExpression else ::TagCreationExpression

        return IZenSymbol { constructor(it, tagType, tagName, environment) }
    }

    override fun getReturnedClass() = ZenTag::class.java
    override fun getRegexMatchingString() = Regex("tag-[a-z]+:[a-z]+:[a-z/]+").toString()

    private fun List<Token>.isStructureValid(): Boolean {
        if (this.count() < 7) return false
        if (this[0].value != "tag") return false
        if (this[1].type != ZenTokener.T_MINUS) return false
        if (this[3].type != ZenTokener.T_COLON) return false
        if (this[5].type != ZenTokener.T_COLON) return false
        return true
    }

    private fun List<Token>.join() = this.joinToString(separator = "") { it.value }
}
