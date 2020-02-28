@file:JvmName("SeqFunExpr")

package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.sequence

import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.BiFunction
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.sequence.ZenSequence
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionInt
import stanhebben.zenscript.expression.ExpressionJavaLambdaSimpleGeneric
import stanhebben.zenscript.expression.ExpressionNull
import stanhebben.zenscript.expression.partial.IPartialExpression
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.util.ZenPosition
import stanhebben.zenscript.util.ZenTypeUtil

abstract class FunctionUpdateSequenceExpression(position: ZenPosition?, protected var function: Expression,
                                                protected var expressionInvocationData: ExpressionInvocationData) : Expression(position) {

    protected open fun updateFunction(sequence: SequenceZenType) {
        val newFunction = (this.function as? ExpressionJavaLambdaSimpleGeneric)?.convert(sequence, this.expressionInvocationData.sequenceTargetPositions) ?: this.function
        this.function = newFunction
    }
}

class NoArgumentSameSequenceReturnTypeSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val sequence: SequenceZenType,
                                                                 private val targetMethodName: String) : Expression(position) {

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)
        environment?.output?.invokeVirtual(ZenSequence::class.java, this.targetMethodName, ZenSequence::class.java)

        if (!result) environment?.output?.pop()
    }

    override fun getType(): ZenType = this.sequence
}

class NoArgumentKnownReturnTypeSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val returnType: ReturnTypeData,
                                                          private val targetMethodName: String) : Expression(position) {
    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)
        environment?.output?.invokeVirtual(ZenSequence::class.java, this.targetMethodName, this.returnType.targetClass.java)

        if (!result && this.returnType.targetClass != Void.TYPE.kotlin) environment?.output?.pop(this.type.isLarge)
    }

    override fun getType(): ZenType = this.returnType.correspondingZenType
}

class NoArgumentGenericReturnTypeSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val sequenceType: SequenceZenType,
                                                            private val targetMethodName: String) : Expression(position) {
    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)
        environment?.output?.invokeVirtual(ZenSequence::class.java, this.targetMethodName, Object::class.java)

        if (!result && this.type.toJavaClass().kotlin != Void.TYPE.kotlin) {
            environment?.output?.pop(this.type.isLarge)
            return
        }

        environment?.output?.checkCast(this.type.toJavaClass())
    }

    override fun getType(): ZenType = this.sequenceType.genericType
}

class SingleArgumentDifferentSequenceReturnTypeSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, sequence: SequenceZenType,
                                                                          function: Expression, expressionInvocationData: ExpressionInvocationData) :
        FunctionUpdateSequenceExpression(position,
                if (function is ExpressionJavaLambdaSimpleGeneric) TypeConverterFunctionExpression(function, sequence, expressionInvocationData.sequenceTargetPositions) else function,
                expressionInvocationData) {

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)

        val type = evaluation.type
        if (type is SequenceZenType) this.updateFunction(type)

        this.function.compile(true, environment)
        environment?.output?.invokeVirtual(ZenSequence::class.java, this.expressionInvocationData.name, ZenSequence::class.java, this.expressionInvocationData.argumentClass.java)

        if (!result) environment?.output?.pop()
    }

    override fun getType(): ZenType =
            if (this.function is TypeConverterFunctionExpression) SequenceZenType { (this.function as TypeConverterFunctionExpression).genericReturnType } else SequenceZenType(ZenType.ANY)

    override fun updateFunction(sequence: SequenceZenType) {
        if (sequence.genericType == ZenType.ANY) return
        this.function = (this.function as? TypeConverterFunctionExpression)?.convert(sequence) ?: this.function
    }

    private fun TypeConverterFunctionExpression.convert(sequence: SequenceZenType) =
            TypeConverterFunctionExpression(this, sequence, expressionInvocationData.sequenceTargetPositions)
}

class SingleArgumentSameSequenceReturnTypeSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val sequence: SequenceZenType,
                                                                     function: Expression, expressionInvocationData: ExpressionInvocationData) :
        FunctionUpdateSequenceExpression(position, function, expressionInvocationData) {

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)

        val type = evaluation.type
        if (type is SequenceZenType) this.updateFunction(type)

        this.function.wrap().compile(true, environment)
        environment?.output?.invokeVirtual(ZenSequence::class.java, this.expressionInvocationData.name, ZenSequence::class.java, this.expressionInvocationData.argumentClass.java)

        if (!result) environment?.output?.pop()
    }

    override fun getType(): ZenType = this.sequence
}

class SingleArgumentKnownReturnTypeSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val returnType: ReturnTypeData,
                                                              function: Expression, expressionInvocationData: ExpressionInvocationData) :
        FunctionUpdateSequenceExpression(position, function, expressionInvocationData) {

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)

        val type = evaluation.type
        if (type is SequenceZenType) this.updateFunction(type)

        this.function.wrap().compile(true, environment)
        environment?.output?.invokeVirtual(ZenSequence::class.java, this.expressionInvocationData.name, this.returnType.targetClass.java, this.expressionInvocationData.argumentClass.java)

        if (!result && this.returnType.targetClass != Void.TYPE.kotlin) environment?.output?.pop(this.type.isLarge)
    }

    override fun getType(): ZenType = this.returnType.correspondingZenType
}

class SingleArgumentGenericReturnTypeSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val sequenceType: SequenceZenType,
                                                                function: Expression, expressionInvocationData: ExpressionInvocationData) :
        FunctionUpdateSequenceExpression(position, function, expressionInvocationData) {

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)

        val type = evaluation.type
        if (type is SequenceZenType) this.updateFunction(type)

        this.function.wrap().compile(true, environment)
        environment?.output?.invokeVirtual(ZenSequence::class.java, this.expressionInvocationData.name, Object::class.java, this.expressionInvocationData.argumentClass.java)

        if (!result && this.type.toJavaClass().kotlin != Void.TYPE.kotlin) {
            environment?.output?.pop(this.type.isLarge)
            return
        }

        environment?.output?.checkCast(this.type.toJavaClass())
    }

    override fun getType(): ZenType = this.sequenceType.genericType
}

class SingleNonFunctionArgumentSameSequenceReturnTypeSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val sequence: SequenceZenType,
                                                                                private val argumentData: ArgumentData, private val methodName: String) : Expression(position) {

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)

        this.argumentData.expression.let { if (this.argumentData.argumentClass == Object::class) it.cast(this.position, environment, ObjectZenType) else it }.compile(true, environment)
        environment?.output?.invokeVirtual(ZenSequence::class.java, this.methodName, ZenSequence::class.java, this.argumentData.argumentClass.java)

        if (!result) environment?.output?.pop()
    }

    override fun getType(): ZenType = this.sequence
}

class SingleNonFunctionArgumentGenericReturnTypeSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val sequence: SequenceZenType,
                                                                           private val argumentData: ArgumentData, private val methodName: String) : Expression(position) {

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)

        this.argumentData.expression.let { if (this.argumentData.argumentClass == Object::class) it.cast(this.position, environment, ObjectZenType) else it }.compile(true, environment)
        environment?.output?.invokeVirtual(ZenSequence::class.java, this.methodName, Object::class.java, this.argumentData.argumentClass.java)

        if (!result && this.type.toJavaClass().kotlin != Void.TYPE.kotlin) {
            environment?.output?.pop(this.type.isLarge)
            return
        }

        environment?.output?.checkCast(ZenTypeUtil.internal(this.type.toJavaClass()))
    }

    override fun getType(): ZenType = this.sequence.genericType
}

class SingleGenericArgumentSameSequenceReturnTypeSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val sequence: SequenceZenType,
                                                                            private val isArray: Boolean, private val argumentExpression: Expression, private val methodName: String)
    : Expression(position) {

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)

        this.argumentExpression.let { if (!this.isArray) it.cast(this.position, environment, ObjectZenType) else it }.compile(true, environment)

        // I need to make sure that [Ljava/lang/Object;.class is used, so an array of Objects. Using Array<Any> has the same result, but poses probable problems in case the Kotlin impl changes.
        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        environment?.output?.invokeVirtual(ZenSequence::class.java, this.methodName, ZenSequence::class.java, (if (this.isArray) Array<Object>::class else Object::class).java)

        if (!result) environment?.output?.pop()
    }

    override fun getType(): ZenType = this.sequence
}

class SingleGenericArgumentKnownReturnTypeSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val argumentExpression: Expression,
                                                                     private val returnType: ReturnTypeData, private val methodName: String) : Expression(position) {

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)

        this.argumentExpression.cast(this.position, environment, ObjectZenType).compile(true, environment)
        environment?.output?.invokeVirtual(ZenSequence::class.java, this.methodName, this.returnType.targetClass.java, Object::class.java)

        if (!result && this.type.toJavaClass().kotlin != Void.TYPE.kotlin) environment?.output?.pop(this.type.isLarge)
    }

    override fun getType(): ZenType = this.returnType.correspondingZenType
}

class DoubleNonFunctionFunctionArgumentsGenericReturnTypeSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val sequence: SequenceZenType,
                                                                                    private val firstArgument: ArgumentData, private val secondArgument: ArgumentData,
                                                                                    private val methodName: String, private val sequenceTargetPositions: List<Int>) : Expression(position) {

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)

        val type = evaluation.type
        val functionExpression = if (type is SequenceZenType) this.updateFunction(type, this.secondArgument.expression) else this.secondArgument.expression

        this.firstArgument.expression.compile(true, environment)
        functionExpression.wrap().compile(true, environment)
        environment?.output?.invokeVirtual(ZenSequence::class.java, this.methodName, Object::class.java, this.firstArgument.argumentClass.java, this.secondArgument.argumentClass.java)

        if (!result && this.type.toJavaClass().kotlin != Void.TYPE.kotlin) {
            environment?.output?.pop(this.type.isLarge)
            return
        }

        environment?.output?.checkCast(this.type.toJavaClass())
    }

    override fun getType(): ZenType = this.sequence.genericType

    private fun updateFunction(sequence: SequenceZenType, oldFunction: Expression): Expression {
        return (oldFunction as? ExpressionJavaLambdaSimpleGeneric)?.convert(sequence, this.sequenceTargetPositions) ?: oldFunction
    }
}

class FoldSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, @Suppress("unused") private val sequence: SequenceZenType,
                                     private val firstArgumentExpression: Expression, secondArgumentFunction: Expression, expressionInvocationData: ExpressionInvocationData)
    : FunctionUpdateSequenceExpression(position, secondArgumentFunction, expressionInvocationData) {

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)

        val type = evaluation.type
        if (type is SequenceZenType) this.updateFunction(type)

        this.firstArgumentExpression.cast(this.position, environment, ObjectZenType).compile(true, environment)
        this.function.wrap().compile(true, environment)
        environment?.output?.invokeVirtual(ZenSequence::class.java, this.expressionInvocationData.name, Object::class.java, Object::class.java, BiFunction::class.java)

        if (!result) environment?.output?.pop()
    }

    // TODO("Automatic infer, maybe?")
    override fun getType(): ZenType = ZenType.ANY
}

class JoinToStringSequenceFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val separatorExpression: Expression?,
                                             private val prefixExpression: Expression?, private val postfixExpression: Expression?, private val limitExpression: Expression?,
                                             private val truncatedExpression: Expression?, private val transformFunction: Expression?,
                                             private val sequenceTargetPositions: List<Int>) : Expression(position) {

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        val evaluation = this.callee?.eval(environment) ?: return
        evaluation.compile(true, environment)

        val type = evaluation.type
        val transform = this.transformFunction?.let { if (type is SequenceZenType) this.updateFunction(type, it) else it }.orNull()

        this.separatorExpression.orNull().compile(true, environment)
        this.prefixExpression.orNull().compile(true, environment)
        this.postfixExpression.orNull().compile(true, environment)
        this.limitExpression.or(ExpressionInt(this.position, -1L, ZenType.INT)).compile(true, environment)
        this.truncatedExpression.orNull().compile(true, environment)
        transform.wrap().compile(true, environment)

        environment?.output?.invokeVirtual(ZenSequence::class.java, "joinToString", String::class.java, String::class.java, String::class.java, String::class.java,
                Integer::class.java, String::class.java, Function::class.java)

        if (!result) environment?.output?.pop()
    }

    override fun getType(): ZenType = ZenType.STRING

    private fun updateFunction(sequence: SequenceZenType, oldFunction: Expression): Expression {
        return (oldFunction as? ExpressionJavaLambdaSimpleGeneric)?.convert(sequence, this.sequenceTargetPositions) ?: oldFunction
    }

    private fun Expression?.orNull() = this.or(ExpressionNull(this@JoinToStringSequenceFunctionExpression.position))
    private fun Expression?.or(other: Expression) = this ?: other
}
