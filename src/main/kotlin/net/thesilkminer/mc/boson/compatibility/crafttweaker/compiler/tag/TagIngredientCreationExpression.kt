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

package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag

import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTagIngredient
import net.thesilkminer.mc.boson.mod.common.recipe.TagIngredient
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.util.ZenPosition

internal class TagIngredientCreationExpression(position: ZenPosition?, private val tagTypeRepresentation: String, private val tagNameRepresentation: String,
                                               private val backupEnvironment: IEnvironmentGlobal?) : Expression(position) {
    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        // Constructor invocation, so we need to prepare all of that
        environment?.output?.let {
            it.newObject(TagIngredient::class.java)
            it.dup()
        }

        // To create a tag ingredient, we first need to create the tag
        val tagCreationExpression = TagCreationExpression(this.position, this.tagTypeRepresentation, this.tagNameRepresentation, this.backupEnvironment)
        tagCreationExpression.compile(true, environment)

        // And now onto the rest
        environment?.output?.let {
            it.invokeStatic("net/thesilkminer/mc/boson/compatibility/crafttweaker/WrapUnwrap", "toNative",
                    "(Lnet/thesilkminer/mc/boson/compatibility/crafttweaker/tag/ZenTag;)Lnet/thesilkminer/mc/boson/api/tag/Tag;")
            it.construct(TagIngredient::class.java, Tag::class.java)
            it.invokeStatic("net/thesilkminer/mc/boson/compatibility/crafttweaker/WrapUnwrap", "toZen",
                    "(Lnet/thesilkminer/mc/boson/mod/common/recipe/TagIngredient;)Lnet/thesilkminer/mc/boson/compatibility/crafttweaker/tag/ZenTagIngredient;")

            if (!result) it.pop(this.type.isLarge)
        }
    }

    override fun getType(): ZenType = this.backupEnvironment?.getType(ZenTagIngredient::class.java) ?: throw IllegalStateException("ZenTagIngredient wasn't registered")
}
