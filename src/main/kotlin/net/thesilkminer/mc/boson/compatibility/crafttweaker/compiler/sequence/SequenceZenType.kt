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

import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.sequence.ZenSequence
import org.objectweb.asm.Type
import stanhebben.zenscript.annotations.CompareType
import stanhebben.zenscript.annotations.OperatorType
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionCallStatic
import stanhebben.zenscript.expression.ExpressionCallVirtual
import stanhebben.zenscript.expression.ExpressionInvalid
import stanhebben.zenscript.expression.partial.IPartialExpression
import stanhebben.zenscript.type.IZenIterator
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.type.casting.ICastingRuleDelegate
import stanhebben.zenscript.type.natives.IJavaMethod
import stanhebben.zenscript.type.natives.JavaMethod
import stanhebben.zenscript.util.ZenPosition
import stanhebben.zenscript.util.ZenTypeUtil
import java.lang.reflect.ParameterizedType

internal class SequenceZenType(private var zenTypeSupplier: () -> ZenType) : ZenType() {
    internal constructor(zenType: ZenType) : this({ zenType })

    internal var genericType: ZenType
        get() = this.zenTypeSupplier()
        private set(value) { this.zenTypeSupplier = { value } }

    override fun unary(position: ZenPosition?, environment: IEnvironmentGlobal?, value: Expression?, operator: OperatorType?): Expression =
            ExpressionInvalid(position).apply { environment?.error(position, "Sequences don't support the unary operator $operator") }

    override fun binary(position: ZenPosition?, environment: IEnvironmentGlobal?, left: Expression?, right: Expression?, operator: OperatorType?): Expression =
            ExpressionInvalid(position).apply { environment?.error(position, "Sequences don't support the binary operator $operator") }

    override fun trinary(position: ZenPosition?, environment: IEnvironmentGlobal?, first: Expression?, second: Expression?, third: Expression?, operator: OperatorType?): Expression =
            ExpressionInvalid(position).apply { environment?.error(position, "Sequences don't support the ternary operator $operator") }

    override fun compare(position: ZenPosition?, environment: IEnvironmentGlobal?, left: Expression?, right: Expression?, type: CompareType?): Expression =
            ExpressionInvalid(position).apply { environment?.error(position, "Sequences can't be compared with $type") }

    override fun getMember(position: ZenPosition?, environment: IEnvironmentGlobal?, value: IPartialExpression?, name: String?): IPartialExpression =
            this.getMember(position, environment, value, name, true)

    override fun getStaticMember(position: ZenPosition?, environment: IEnvironmentGlobal?, name: String?): IPartialExpression =
            ExpressionInvalid(position).apply { environment?.error(position, "No such static member with name '$name' exists in a Sequence") }

    override fun call(position: ZenPosition?, environment: IEnvironmentGlobal?, receiver: Expression?, vararg arguments: Expression?): Expression =
            if (receiver != null) ExpressionInvalid(position).apply { environment?.error(position, "Cannot call a Sequence") }
            else NewSequenceExpression(position, this.genericType, environment, arguments.toList())

    override fun constructCastingRules(environment: IEnvironmentGlobal?, rules: ICastingRuleDelegate?, followCasters: Boolean) = Unit
    override fun makeIterator(numValues: Int, methodOutput: IEnvironmentMethod?): IZenIterator? = null
    override fun toJavaClass(): Class<*> = ZenSequence::class.java
    override fun toASMType(): Type = Type.getType(this.toJavaClass())
    override fun getNumberType(): Int = 0
    override fun getSignature(): String = ZenTypeUtil.signature(this.toJavaClass())
    override fun isPointer(): Boolean = true
    override fun getAnyClassName(environment: IEnvironmentGlobal?): String = this.name
    override fun getName(): String = this.toJavaClass().canonicalName
    override fun defaultValue(position: ZenPosition?): Expression? = null

    private fun getMember(position: ZenPosition?, environment: IEnvironmentGlobal?, value: IPartialExpression?, name: String?, recursionGuard: Boolean): IPartialExpression {
        if (recursionGuard && this.genericType == ANY) {
            return this.updateType(value, environment).getMember(position, environment, value, name, false)
        }

        if (name in methodDataMap.keys) {
            return SequenceMemberPartialExpression(position, name!!, environment, this, value)
        }

        environment?.error(position, "No such member with name '$name' exists in Sequences")
        return ExpressionInvalid(position)
    }

    private fun updateType(value: IPartialExpression?, environment: IEnvironmentGlobal?): SequenceZenType {
        val method = when (value) {
            is ExpressionCallStatic -> value.method
            is ExpressionCallVirtual -> value.method
            else -> null
        } ?: return this

        if (method is JavaMethod) {
            val genericReturnType = method.method.genericReturnType
            if (genericReturnType is ParameterizedType) {
                environment?.getType(genericReturnType.actualTypeArguments[0])?.let { return SequenceZenType(it) }
            }
        }

        return this
    }

    private val ExpressionCallStatic.method get() = this::class.java.getDeclaredField("method").apply { this.isAccessible = true }.get(this).uncheckedCast<IJavaMethod>()
    private val ExpressionCallVirtual.method get() = this::class.java.getDeclaredField("method").apply { this.isAccessible = true }.get(this).uncheckedCast<IJavaMethod>()
}
