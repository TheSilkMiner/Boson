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

package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.sequence

import net.thesilkminer.mc.boson.compatibility.crafttweaker.preprocessor.ExperimentalFlag
import net.thesilkminer.mc.boson.compatibility.crafttweaker.preprocessor.flagsForCurrentScript
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.sequence.ZenSequence
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionArray
import stanhebben.zenscript.expression.ExpressionCallStatic
import stanhebben.zenscript.expression.ExpressionCallVirtual
import stanhebben.zenscript.expression.ExpressionLocalGet
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.type.ZenTypeArray
import stanhebben.zenscript.type.ZenTypeArrayBasic
import stanhebben.zenscript.type.ZenTypeArrayList
import stanhebben.zenscript.util.ZenPosition
import stanhebben.zenscript.util.ZenTypeUtil

internal class NewSequenceExpression(position: ZenPosition?, private val genericType: ZenType, private val backupEnvironment: IEnvironmentGlobal?, expressionList: List<Expression?>)
    : Expression(position) {
    private val constructorExpression = this.obtainNeededExpression(position, this.genericType, expressionList)

    override fun getType(): ZenType = SequenceZenType(this.genericType)

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) = if (result) this.performCompilation(environment) else Unit

    private fun performCompilation(environment: IEnvironmentMethod?) {
        environment?.output?.let {
            it.newObject(ZenSequence::class.java)
            it.dup()
            this.constructorExpression.compile(true, environment)
            val descriptor = if (this.constructorExpression.type is ZenTypeArrayList) "(Ljava/util/List;)V" else "([Ljava/lang/Object;)V"
            it.invokeSpecial(ZenTypeUtil.internal(ZenSequence::class.java), "<init>", descriptor)
        }
    }

    private fun obtainNeededExpression(position: ZenPosition?, genericType: ZenType, expressionList: List<Expression?>): Expression {
        val expressions = expressionList.filterNotNull()
        if (expressions.count() > 1 || ExperimentalFlag.SEQUENCE_ARRAY_ARGUMENT_OBTAINING !in flagsForCurrentScript) {
            return ExpressionArray(position, ZenTypeArrayBasic(genericType), *expressions.map { it.cast(position, this.backupEnvironment, this.genericType) }.toTypedArray())
        }
        return expressions[0].convertToProperExpression(position, genericType)
    }

    private fun Expression.convertToProperExpression(position: ZenPosition?, genericType: ZenType): Expression {
        if (this is ExpressionCallStatic || this is ExpressionCallVirtual || this is ExpressionLocalGet) {
            val returnType = this.type
            if (returnType is ZenTypeArray && returnType.baseType == genericType) {
                // If the return type returns an array, and the type of that array is actually the generic one
                return this
            } else if (returnType == genericType) {
                // Otherwise, if the return type is the generic type, we wrap that into an array
                return ExpressionArray(position, ZenTypeArrayBasic(genericType), this) // No need to cast since the type is already correct
            }
        }
        // This will cause compile errors later down the line, so... nevermind
        return ExpressionArray(position, ZenTypeArrayBasic(genericType), this.cast(position, this@NewSequenceExpression.backupEnvironment, this@NewSequenceExpression.genericType))
    }
}
