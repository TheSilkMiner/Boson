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

    override fun call(position: ZenPosition?, environment: IEnvironmentMethod?, vararg values: Expression?): Expression {
        val genericType = this.getGenericType(environment)
        val expressionList = values.map { it?.cast(position, environment, genericType) }
        return NewSequenceExpression(position, genericType, expressionList)
    }

    private fun getGenericType(environment: IEnvironmentGlobal?): ZenType {
        val classNameBits = this.className.split('.')
        val initialValue = environment?.getValue(classNameBits[0], this.position) ?: throw IllegalStateException("null environment")
        val targetValue = classNameBits.asSequence().drop(1).fold(initialValue) { accumulator, nameBit -> accumulator.getMember(this.position, environment, nameBit) }
        return targetValue.toType(environment)
    }
}
