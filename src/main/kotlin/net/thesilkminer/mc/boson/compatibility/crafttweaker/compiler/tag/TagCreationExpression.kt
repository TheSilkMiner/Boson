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

package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag

import net.thesilkminer.mc.boson.api.tag.TagType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTag
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTagType
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.util.ZenPosition

internal class TagCreationExpression(position: ZenPosition?, private val tagTypeRepresentation: String, private val tagNameRepresentation: String,
                                     private val backupEnvironment: IEnvironmentGlobal?) : Expression(position) {

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        environment?.output?.let {
            it.getStaticField(ZenTag::class.java, ZenTag::class.java.getDeclaredField("Companion"))
            it.getStaticField(ZenTagType::class.java, ZenTagType::class.java.getDeclaredField("Companion"))
            it.constant(this.tagTypeRepresentation)
            it.invokeVirtual(ZenTagType.Companion::class.java, "findZenTagType", ZenTagType::class.java, String::class.java)
            it.invokeStatic("net/thesilkminer/mc/boson/compatibility/crafttweaker/WrapUnwrap", "toNative",
                    "(Lnet/thesilkminer/mc/boson/compatibility/crafttweaker/tag/ZenTagType;)Lnet/thesilkminer/mc/boson/api/tag/TagType;")
            it.constant(this.tagNameRepresentation)
            it.invokeVirtual(ZenTag.Companion::class.java, "createAndWrap", ZenTag::class.java, TagType::class.java, String::class.java)
            if (!result) it.pop(this.type.isLarge)
        }
    }

    override fun getType(): ZenType = TagZenType(this.getGenericType(this.backupEnvironment))

    private fun getGenericType(environment: IEnvironmentGlobal?) =
            environment?.getType(this.tagTypeRepresentation.tryGetCustomClass(this.position, environment).java) ?: throw IllegalStateException("Core classes weren't registered!")
}
