package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag

import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionInvalid
import stanhebben.zenscript.expression.partial.IPartialExpression
import stanhebben.zenscript.symbols.IZenSymbol
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.util.ZenPosition

class TagMemberPartialExpression(private val position: ZenPosition?, private val methodName: String,
                                 private val environment: IEnvironmentGlobal?, private val tagType: TagZenType,
                                 private val callee: IPartialExpression?) : IPartialExpression {

    override fun eval(environment: IEnvironmentGlobal?): Expression {
        environment?.warning(this.position, "Tag members cannot be evaluated, assuming it's a method call")
        return this.call(this.position, environment as? IEnvironmentMethod?)
    }

    override fun assign(position: ZenPosition?, environment: IEnvironmentGlobal?, other: Expression?): Expression =
            ExpressionInvalid(position).also { environment?.error(position, "Cannot reassign a tag member") }

    override fun getMember(position: ZenPosition?, environment: IEnvironmentGlobal?, name: String?): IPartialExpression =
            ExpressionInvalid(position).also { environment?.error(position, "Tag members cannot have members inside") }

    override fun call(position: ZenPosition?, environment: IEnvironmentMethod?, vararg values: Expression?): Expression {
        val numArguments = values.count()
        val methodData = methodDataMap[this.methodName] ?: listOf()
        val targetMethodData = methodData.findAll(numArguments).firstOrNull()
        val expressionCreator = targetMethodData?.expressionCreator ?: return ExpressionInvalid(position).also { environment?.error(position, "No such member '${this.methodName}'") }
        return expressionCreator(position, this.callee, values.toList().requireNoNulls(), this.tagType, this.environment)
    }

    override fun predictCallTypes(numArguments: Int): Array<ZenType?> {
        if (numArguments <= 0) return arrayOf()
        val methodData = methodDataMap[this.methodName] ?: listOf()
        val targetMethodData = methodData.findAll(numArguments).firstOrNull()
        val targetArgs = targetMethodData?.arguments ?: return arrayOfNulls(numArguments)
        val types = mutableListOf<ZenType?>()
        targetArgs.forEach { types += it(this.tagType, this.environment) }
        return types.toTypedArray()
    }

    override fun getType(): ZenType? = null
    override fun toType(environment: IEnvironmentGlobal?): ZenType? = null
    override fun toSymbol(): IZenSymbol? = null

    private fun List<TagMethodData>.findAll(numArguments: Int) = sequence {
        this@findAll.findExact(numArguments)?.let { yield(it) }
        this@findAll.findVarargs(numArguments)?.let { yield(it) }
    }

    private fun List<TagMethodData>.findExact(numArguments: Int) = this.find { it.argumentsCount == numArguments }
    private fun List<TagMethodData>.findVarargs(numArguments: Int) = this.find { (it.argumentsCount <= numArguments || it.argumentsCount - 1 == numArguments) && it.hasEndingVararg }
}
