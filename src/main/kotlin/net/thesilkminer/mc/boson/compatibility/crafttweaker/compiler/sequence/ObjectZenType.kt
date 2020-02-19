package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.sequence

import org.objectweb.asm.Type
import stanhebben.zenscript.annotations.CompareType
import stanhebben.zenscript.annotations.OperatorType
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionInvalid
import stanhebben.zenscript.expression.ExpressionNull
import stanhebben.zenscript.expression.partial.IPartialExpression
import stanhebben.zenscript.type.IZenIterator
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.type.casting.ICastingRuleDelegate
import stanhebben.zenscript.util.ZenPosition
import stanhebben.zenscript.util.ZenTypeUtil

object ObjectZenType : ZenType() {
    override fun unary(position: ZenPosition?, environment: IEnvironmentGlobal?, value: Expression?, operator: OperatorType?): Expression =
            ExpressionInvalid(position).apply { environment?.error(position, "The unary operator '$operator' is not supported") }

    override fun binary(position: ZenPosition?, environment: IEnvironmentGlobal?, left: Expression?, right: Expression?, operator: OperatorType?): Expression =
            ExpressionInvalid(position).apply { environment?.error(position, "The binary operator '$operator' is not supported") }

    override fun trinary(position: ZenPosition?, environment: IEnvironmentGlobal?, first: Expression?, second: Expression?, third: Expression?, operator: OperatorType?): Expression =
            ExpressionInvalid(position).apply { environment?.error(position, "The ternary operator '$operator' is not supported") }

    override fun compare(position: ZenPosition?, environment: IEnvironmentGlobal?, left: Expression?, right: Expression?, type: CompareType?): Expression =
            ExpressionInvalid(position).apply { environment?.error(position, "The compare operator of type '$type' is not supported") }

    override fun getMember(position: ZenPosition?, environment: IEnvironmentGlobal?, value: IPartialExpression?, name: String?): IPartialExpression =
            ExpressionInvalid(position).apply { environment?.error(position, "Unable to get the member '$name': not supported") }

    override fun getStaticMember(position: ZenPosition?, environment: IEnvironmentGlobal?, name: String?): IPartialExpression =
            ExpressionInvalid(position).apply { environment?.error(position, "Unable to get the static member '$name': not supported") }

    override fun call(position: ZenPosition?, environment: IEnvironmentGlobal?, receiver: Expression?, vararg arguments: Expression?): Expression =
            ExpressionInvalid(position).apply { environment?.error(position, "Calling this type is not supported") }

    override fun constructCastingRules(environment: IEnvironmentGlobal?, rules: ICastingRuleDelegate?, followCasters: Boolean) = Unit
    override fun makeIterator(numValues: Int, methodOutput: IEnvironmentMethod?): IZenIterator? = null
    override fun toJavaClass(): Class<*> = Object::class.java
    override fun toASMType(): Type = Type.getType(this.toJavaClass())
    override fun getNumberType(): Int = 0
    override fun getSignature(): String = ZenTypeUtil.signature(this.toJavaClass())
    override fun isPointer(): Boolean = true
    override fun getAnyClassName(global: IEnvironmentGlobal?): String = this.name
    override fun getName(): String = this.javaClass.canonicalName
    override fun defaultValue(position: ZenPosition?): Expression = ExpressionNull(position)
}
