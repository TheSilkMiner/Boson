@file:JvmName("TagFunExpr")

package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag

import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTag
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionInvalid
import stanhebben.zenscript.expression.partial.IPartialExpression
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.util.ZenPosition
import stanhebben.zenscript.util.ZenTypeUtil
import kotlin.reflect.KClass

class NoArgumentKnownReturnTypeTagFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val targetMethodName: String,
                                                     private val returnTypeData: ReturnTypeData) : Expression(position) {
    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        this.callee?.eval(environment)?.compile(true, environment) ?: return
        environment?.output?.invokeVirtual(ZenTag::class.java, this.targetMethodName, this.returnTypeData.returnTypeClass.java)

        if (!result && this.returnTypeData.returnTypeClass != Void.TYPE.kotlin) environment?.output?.pop(this.type.isLarge)
    }

    override fun getType(): ZenType = this.returnTypeData.returnType
}

class SingleArgumentKnownReturnTypeTagFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val targetMethodName: String,
                                                         private val returnTypeData: ReturnTypeData, private val argumentData: ArgumentData) : Expression(position) {
    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        this.callee?.eval(environment)?.compile(true, environment) ?: return
        this.argumentData.expression?.compile(true, environment)
        environment?.output?.invokeVirtual(ZenTag::class.java, this.targetMethodName, this.returnTypeData.returnTypeClass.java, this.argumentData.argumentTypeClass.java)

        if (!result && this.returnTypeData.returnTypeClass != Void.TYPE.kotlin) environment?.output?.pop(this.type.isLarge)
    }

    override fun getType(): ZenType = this.returnTypeData.returnType
}

class SingleVarargArgumentKnownReturnTypeTagFunctionExpression(position: ZenPosition?, private val callee: IPartialExpression?, private val targetMethodName: String,
                                                               private val returnTypeData: ReturnTypeData, private val varargArgumentData: ArgumentData,
                                                               private val actualArguments: List<Expression>, private val actualArgumentClass: KClass<*>) : Expression(position) {
    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        this.callee?.eval(environment)?.compile(true, environment) ?: return
        environment?.output?.visitor?.let {
            this.actualArguments.count().visitOpcode(it)
            it.visitTypeInsn(Opcodes.ANEWARRAY, ZenTypeUtil.internal(this.varargArgumentData.argumentTypeClass.java))
            it.visitInsn(Opcodes.NOP)
            this.actualArguments.forEachIndexed { index, expression ->
                it.visitInsn(Opcodes.DUP)
                index.visitOpcode(it)
                expression.compile(true, environment)
                it.visitTypeInsn(Opcodes.CHECKCAST, ZenTypeUtil.internal(this.varargArgumentData.argumentTypeClass.java))
                it.visitInsn(Opcodes.AASTORE)
                it.visitInsn(Opcodes.NOP)
            }
        }
        environment?.output?.invokeVirtual(ZenTag::class.java, this.targetMethodName, this.returnTypeData.returnTypeClass.java, this.actualArgumentClass.java)

        if (!result && this.returnTypeData.returnTypeClass != Void.TYPE.kotlin) environment?.output?.pop(this.type.isLarge)
    }

    override fun getType(): ZenType = this.returnTypeData.returnType

    private fun Int.visitOpcode(mv: MethodVisitor) = when (this) {
        -1 -> mv.visitInsn(Opcodes.ICONST_M1)
        0 -> mv.visitInsn(Opcodes.ICONST_0)
        1 -> mv.visitInsn(Opcodes.ICONST_1)
        2 -> mv.visitInsn(Opcodes.ICONST_2)
        3 -> mv.visitInsn(Opcodes.ICONST_3)
        4 -> mv.visitInsn(Opcodes.ICONST_4)
        5 -> mv.visitInsn(Opcodes.ICONST_5)
        else -> this.visitBiPushSiPushLdc(mv)
    }

    private fun Int.visitBiPushSiPushLdc(mv: MethodVisitor) = when (this) {
        in -128..127 -> this.visitBiPush(mv)
        in -32768..32767 -> this.visitSiPush(mv)
        else -> this.visitLdc(mv)
    }

    private fun Int.visitBiPush(mv: MethodVisitor) = mv.visitIntInsn(Opcodes.BIPUSH, this)
    private fun Int.visitSiPush(mv: MethodVisitor) = mv.visitIntInsn(Opcodes.SIPUSH, this)
    private fun Int.visitLdc(mv: MethodVisitor) = mv.visitLdcInsn(Integer.valueOf(this))
}

class OverloadedSingleArgumentTagFunctionExpression(position: ZenPosition?, private val arguments: List<Expression>, private val overloads: List<OverloadPathExpressionData>,
                                                    private val backupEnvironment: IEnvironmentGlobal?) : Expression(position) {
    private val selectedTarget = this.overloads.choose(this.arguments, this.backupEnvironment)

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) = this.selectedTarget.compile(result, environment)
    override fun getType(): ZenType = this.selectedTarget.type

    private fun List<OverloadPathExpressionData>.choose(arguments: List<Expression>, environment: IEnvironmentGlobal?) = this
            .filter { it.predicate(arguments) }
            .groupBy { it.priority }
            .asSequence()
            .maxBy { it.key }
            ?.value
            ?.asSequence()
            ?.singleOrNull()
            ?.validExpression
            .orInvalid(environment)

    private fun Expression?.orInvalid(environment: IEnvironmentGlobal?) =
            this ?: ExpressionInvalid(this@OverloadedSingleArgumentTagFunctionExpression.position).also {
                environment?.error(this@OverloadedSingleArgumentTagFunctionExpression.position,
                        "Multiple overloads with the same priority are present for this method call: unable to continue")
            }
}
