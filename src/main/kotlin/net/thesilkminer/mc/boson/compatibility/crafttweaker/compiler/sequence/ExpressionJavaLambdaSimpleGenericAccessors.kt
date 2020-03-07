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
