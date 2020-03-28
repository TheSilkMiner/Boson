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

@file:JvmName("EJLSGA")

package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.sequence

import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import stanhebben.zenscript.definitions.ParsedFunctionArgument
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionJavaLambdaSimpleGeneric
import stanhebben.zenscript.statements.Statement

internal val ExpressionJavaLambdaSimpleGeneric.arguments get() = this.find<List<ParsedFunctionArgument>>("arguments")
internal val ExpressionJavaLambdaSimpleGeneric.interfaceClass get() = this.find<Class<*>>("interfaceClass")
internal val ExpressionJavaLambdaSimpleGeneric.genericClass get() = this.find<Class<*>>("genericClass")
internal val ExpressionJavaLambdaSimpleGeneric.descriptor get() = this.find<String>("descriptor")
internal val ExpressionJavaLambdaSimpleGeneric.statements get() = this.find<List<Statement>>("statements")

internal fun Expression.wrap() = if (this is ExpressionJavaLambdaSimpleGeneric) SimpleGenericLambdaFunctionExpression(this) else this

private inline fun <reified T> ExpressionJavaLambdaSimpleGeneric.find(fieldName: String)
        = this::class.java.getDeclaredField(fieldName).apply { this.isAccessible = true }.get(this).uncheckedCast<T>()
