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

import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTag
import org.objectweb.asm.Type
import stanhebben.zenscript.annotations.CompareType
import stanhebben.zenscript.annotations.OperatorType
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionInvalid
import stanhebben.zenscript.expression.partial.IPartialExpression
import stanhebben.zenscript.type.IZenIterator
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.type.casting.ICastingRuleDelegate
import stanhebben.zenscript.util.ZenPosition
import stanhebben.zenscript.util.ZenTypeUtil

internal class TagZenType(private var genericTypeSupplier: () -> ZenType) : ZenType() {
    internal constructor(genericType: ZenType) : this ({ genericType })

    internal val genericType: ZenType get() = this.genericTypeSupplier()

    override fun unary(position: ZenPosition?, environment: IEnvironmentGlobal?, value: Expression?, operator: OperatorType?): Expression = when (operator) {
        OperatorType.NEG -> this.redirectToMethod(position, environment, value, "unaryMinus")
        else -> ExpressionInvalid(position).apply { environment?.error(position, "Tags don't support the unary operator $operator") }
    }

    override fun binary(position: ZenPosition?, environment: IEnvironmentGlobal?, left: Expression?, right: Expression?, operator: OperatorType?): Expression = when (operator) {
        OperatorType.ADD, OperatorType.CAT, OperatorType.AND -> this.redirectToMethod(position, environment, left, "plusAssign", right)
        OperatorType.SUB -> this.redirectToMethod(position, environment, left, "minusAssign", right)
        OperatorType.CONTAINS -> this.redirectToMethod(position, environment, left, "contains", right)
        OperatorType.EQUALS -> this.redirectToMethod(position, environment, left, "equals", right)
        else -> ExpressionInvalid(position).apply { environment?.error(position, "Tags don't support the binary operator $operator") }
    }

    override fun trinary(position: ZenPosition?, environment: IEnvironmentGlobal?, first: Expression?, second: Expression?, third: Expression?, operator: OperatorType?): Expression =
            ExpressionInvalid(position).apply { environment?.error(position, "Tags don't support the ternary operator $operator") }

    override fun compare(position: ZenPosition?, environment: IEnvironmentGlobal?, left: Expression?, right: Expression?, type: CompareType?): Expression =
            ExpressionInvalid(position).apply { environment?.error(position, "Tags can't be compared with $type") }

    override fun getMember(position: ZenPosition?, environment: IEnvironmentGlobal?, value: IPartialExpression?, name: String?): IPartialExpression {
        if (name != null && name in methodDataMap.keys) {
            return TagMemberPartialExpression(position, name, environment, this, value)
        }

        if (name != null && "get${name.capitalize()}" in methodDataMap.keys) {
            return TagMemberPartialExpression(position, "get${name.capitalize()}", environment, this, value)
        }

        environment?.error(position, "No such member with name '${value}' exists in tags")
        return ExpressionInvalid(position)
    }

    override fun getStaticMember(position: ZenPosition?, environment: IEnvironmentGlobal?, name: String?): IPartialExpression =
            ExpressionInvalid(position).apply { environment?.error(position, "No static member with name '$name' exists in a Tag") }

    override fun call(position: ZenPosition?, environment: IEnvironmentGlobal?, receiver: Expression?, vararg arguments: Expression?): Expression =
            this.redirectToMethod(position, environment, receiver, "invoke")

    override fun constructCastingRules(environment: IEnvironmentGlobal?, rules: ICastingRuleDelegate?, followCasters: Boolean) = Unit

    override fun makeIterator(numValues: Int, methodOutput: IEnvironmentMethod?): IZenIterator? = if (numValues == 1) TagZenIterator(methodOutput?.output, this) else null

    override fun toJavaClass(): Class<*> = ZenTag::class.java
    override fun toASMType(): Type = Type.getType(this.toJavaClass())
    override fun getNumberType(): Int = 0
    override fun getSignature(): String = ZenTypeUtil.signature(this.toJavaClass())
    override fun isPointer(): Boolean = true
    override fun getAnyClassName(environment: IEnvironmentGlobal?): String = this.name
    override fun getName(): String = this.toJavaClass().canonicalName
    override fun defaultValue(position: ZenPosition?): Expression? = null

    private fun redirectToMethod(position: ZenPosition?, environment: IEnvironmentGlobal?, callee: IPartialExpression?, methodName: String, vararg arguments: Expression?): Expression =
            this.getMember(position, environment, callee, methodName).call(position, environment.toMethodEnvironment(), *arguments)

    private fun IEnvironmentGlobal?.toMethodEnvironment() = this as? IEnvironmentMethod
}
