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

import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionInvalid
import stanhebben.zenscript.expression.partial.IPartialExpression
import stanhebben.zenscript.symbols.IZenSymbol
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.util.ZenPosition

internal class SequencePartialExpression(position: ZenPosition, private val className: String, private val backupEnvironment: IEnvironmentGlobal) : Expression(position) {
    override fun compile(result: Boolean, environment: IEnvironmentMethod?) = Unit
    override fun eval(environment: IEnvironmentGlobal?): Expression = this
    override fun predictCallTypes(numArguments: Int): Array<ZenType> = Array(numArguments) { this.getGenericType(this.backupEnvironment) }
    override fun getType(): ZenType = this.getGenericType(this.backupEnvironment)
    override fun toType(environment: IEnvironmentGlobal?): ZenType = SequenceZenType(this.getGenericType(environment))
    override fun toSymbol(): IZenSymbol? = null

    override fun getMember(position: ZenPosition?, environment: IEnvironmentGlobal?, name: String?): IPartialExpression = ExpressionInvalid(position).apply {
        environment?.error(position, "Bracket handlers cannot have members")
    }

    override fun call(position: ZenPosition?, environment: IEnvironmentMethod?, vararg values: Expression?): Expression =
        NewSequenceExpression(position, this.getGenericType(environment), environment, values.toList())

    private fun getGenericType(environment: IEnvironmentGlobal?): ZenType {
        val classNameBits = this.className.split('.')
        val initialValue = environment?.getValue(classNameBits[0], this.position) ?: throw IllegalStateException("null environment")
        val targetValue = classNameBits.asSequence().drop(1).fold(initialValue) { accumulator, nameBit -> accumulator.getMember(this.position, environment, nameBit) }
        return targetValue.toType(environment)
    }
}
