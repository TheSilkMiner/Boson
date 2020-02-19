package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.sequence

import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.sequence.ZenSequence
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionArray
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.type.ZenTypeArrayBasic
import stanhebben.zenscript.util.ZenPosition
import stanhebben.zenscript.util.ZenTypeUtil

class NewSequenceExpression(position: ZenPosition?, private val genericType: ZenType, expressionList: List<Expression?>) : Expression(position) {
    private val expressionArray = ExpressionArray(position, ZenTypeArrayBasic(this.genericType), *expressionList.filterNotNull().toTypedArray())

    override fun getType(): ZenType = SequenceZenType(this.genericType)

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) = if (result) this.performCompilation(environment) else Unit

    private fun performCompilation(environment: IEnvironmentMethod?) {
        environment?.output?.let {
            it.newObject(ZenSequence::class.java)
            it.dup()
            this.expressionArray.compile(true, environment)
            it.invokeSpecial(ZenTypeUtil.internal(ZenSequence::class.java), "<init>", "([Ljava/lang/Object;)V")
        }
    }
}
