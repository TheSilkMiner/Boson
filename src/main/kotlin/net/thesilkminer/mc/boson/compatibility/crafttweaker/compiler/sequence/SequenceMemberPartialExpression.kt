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

package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.sequence

import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionInvalid
import stanhebben.zenscript.expression.partial.IPartialExpression
import stanhebben.zenscript.symbols.IZenSymbol
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.util.ZenPosition

internal class SequenceMemberPartialExpression(private val position: ZenPosition?, private val methodName: String,
                                               private val environment: IEnvironmentGlobal?, private val sequenceZenType: SequenceZenType,
                                               private val callee: IPartialExpression?) : IPartialExpression {

    override fun eval(environment: IEnvironmentGlobal?): Expression =
            ExpressionInvalid(this.position).apply { environment?.error(this.position, "Members cannot be evaluated") }

    override fun assign(position: ZenPosition?, environment: IEnvironmentGlobal?, other: Expression?): Expression =
            ExpressionInvalid(position).apply { environment?.error(position, "Members cannot be assigned") }

    override fun getMember(position: ZenPosition?, environment: IEnvironmentGlobal?, name: String?): IPartialExpression =
            ExpressionInvalid(position).apply { environment?.error(position, "Members don't have members") }

    override fun call(position: ZenPosition?, environment: IEnvironmentMethod?, vararg values: Expression?): Expression {
        val argumentsLength = values.count()
        val methodData = methodDataMap[this.methodName] ?: listOf()
        val expressionCreator = methodData.find { it.argumentsCount == argumentsLength }?.expressionCreator
                ?: return ExpressionInvalid(position).apply { environment?.error("No such member: '${this@SequenceMemberPartialExpression.methodName}'") }
        return expressionCreator(position, this.callee, values.toList().requireNoNulls(), this.sequenceZenType)
    }

    override fun predictCallTypes(numArguments: Int): Array<ZenType?> {
        if (numArguments <= 0) return arrayOf()
        val types = mutableListOf<ZenType?>()
        val methodData = methodDataMap[this.methodName] ?: listOf()
        val targetData = methodData.find { it.argumentsCount == numArguments }?.arguments ?: return Array(numArguments) { null }
        targetData.forEach { types += it(this.environment, this.sequenceZenType) }
        return types.toTypedArray()
    }

    override fun toSymbol(): IZenSymbol? = null
    override fun getType(): ZenType? = null
    override fun toType(environment: IEnvironmentGlobal?): ZenType? = null
}
