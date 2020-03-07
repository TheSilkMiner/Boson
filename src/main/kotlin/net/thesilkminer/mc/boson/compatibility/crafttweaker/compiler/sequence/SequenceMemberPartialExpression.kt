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
