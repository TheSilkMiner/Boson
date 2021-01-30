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

import net.thesilkminer.kotlin.commons.lang.plusAssign
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import stanhebben.zenscript.compiler.EnvironmentClass
import stanhebben.zenscript.compiler.EnvironmentMethod
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.compiler.ZenClassWriter
import stanhebben.zenscript.definitions.ParsedFunctionArgument
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionJavaLambdaSimpleGeneric
import stanhebben.zenscript.statements.Statement
import stanhebben.zenscript.statements.StatementReturn
import stanhebben.zenscript.symbols.SymbolArgument
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.util.ZenPosition
import stanhebben.zenscript.util.ZenTypeUtil
import java.lang.reflect.Method
import kotlin.reflect.KClass

internal class TypeConverterFunctionExpression private constructor(position: ZenPosition?, sequence: SequenceZenType, private val interfaceClass: KClass<*>,
                                                                   arguments: MutableList<ParsedFunctionArgument>, private val genericArgumentPositions: List<Int>,
                                                                   private val statements: List<Statement>, private val type: ZenType) : Expression(position) {
    internal constructor(function: TypeConverterFunctionExpression, sequence: SequenceZenType, genericArgumentPositions: List<Int>) :
            this(function.position, sequence, function.interfaceClass, function.arguments.toMutableList(), genericArgumentPositions, function.statements, function.type)

    internal constructor(function: ExpressionJavaLambdaSimpleGeneric, sequence: SequenceZenType, genericArgumentPositions: List<Int>) :
            this(function.position, sequence, function.interfaceClass.kotlin, function.arguments.toMutableList(), genericArgumentPositions, function.statements, function.type)

    companion object {
        private val counters = mutableMapOf<KClass<*>, Int>()

        internal fun getAndIncrement(interfaceClass: KClass<*>): Int {
            val number = this.counters[interfaceClass] ?: 0
            this.counters[interfaceClass] = number + 1
            return number
        }
    }

    private val arguments = arguments.convert(sequence, this.genericArgumentPositions).toList()
    private var genericClasses = this.genericArgumentPositions.asSequence().map { pos -> arguments[pos].type.let { if (it == ZenType.ANY) Object::class else it.toJavaClass().kotlin } }.toList()
    private val descriptor = StringBuilder().apply {
        this += '('
        this@TypeConverterFunctionExpression.arguments.forEachIndexed { index, element ->
            if (element.type == ZenType.ANY) this += ZenTypeUtil.signature(this@TypeConverterFunctionExpression.interfaceClass.java.methods[0].parameterTypes[index])
            else this += element.type.signature
        }
        this += ')'
        this += ZenTypeUtil.signature(this@TypeConverterFunctionExpression.interfaceClass.java.methods[0].returnType)
    }.toString()

    internal var genericReturnType: ZenType = ZenType.ANY
        private set

    override fun getType(): ZenType = this.type

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        if (!result) return
        if (environment == null) return

        val method = this.interfaceClass.java.methods[0]
        val className = "${this.position.file.className}\$sam\$${this.interfaceClass.simpleName?.replace('.', '_') ?: "unknown"}\$${getAndIncrement(this.interfaceClass)}"
                .replace(';', '$')
        val classWriter = ZenClassWriter(ClassWriter.COMPUTE_FRAMES)

        this.generateClass(classWriter, className, environment, method)

        environment.putClass(className, classWriter.toByteArray())

        environment.output.let {
            it.newObject(className)
            it.dup()
            it.construct(className)
        }
    }

    private fun generateClass(v: ClassVisitor, name: String, environment: IEnvironmentMethod, method: Method) {
        v.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER, name,
                this.generateMethodSignature(), "java/lang/Object", arrayOf(ZenTypeUtil.internal(this.interfaceClass.java)))
        v.visitSource(".dynamic.TypeConverterFunctionExpression.asm", null)

        this.generateAnnotations(v, name)

        this.generateConstructor(v, name)
        this.generateMethod(v, environment, method)

        if (this.genericClasses.any { it != Object::class }) {
            val needsSecondaryBridge = this.arguments.any { it.type.isPrimitive }
            this.generateBridge(v, name, method, needsSecondaryBridge)
        }
    }

    private fun generateMethodSignature() = StringBuilder().apply {
        this += "Ljava/lang/Object;"
        this += ZenTypeUtil.signature(this@TypeConverterFunctionExpression.interfaceClass.java)
        this.deleteCharAt(this.length - 1)

        var posIndex = 0

        this += '<'
        this@TypeConverterFunctionExpression.arguments.forEachIndexed { index, argument ->
            if (index in this@TypeConverterFunctionExpression.genericArgumentPositions) {
                this += ZenTypeUtil.signature(this@TypeConverterFunctionExpression.genericClasses[posIndex].java)
                ++posIndex
                return@forEachIndexed
            }
            this += ZenTypeUtil.signature(argument.type.accountForAutobox().toJavaClass())
        }

        this += ZenTypeUtil.signature(Object::class.java)
        this += ">;"
    }.toString()

    private fun generateAnnotations(v: ClassVisitor, name: String) {
        v.visitAnnotation("Lstanhebben/zenscript/annotation/ZenClass;", true).let { zenClass ->
            zenClass.visit("value", name)
            zenClass.visitEnd()
        }

        v.visitAnnotation("Lnet/thesilkminer/mc/boson/compatibility/crafttweaker/compiler/annotation/ZenMetadata;", true).let { zenMetadata ->
            zenMetadata.visit("rv", false.toBoolean())
            zenMetadata.visitArray("ci").let { ci ->
                ci.visit(null, 0.toInteger())
                ci.visit(null, 1.toInteger())
                ci.visit(null, 0.toInteger())
                ci.visitEnd()
            }
            zenMetadata.visitArray("cs").let { cs ->
                cs.visit(null, ":rg")
                cs.visit(null, "sam:${this.interfaceClass.qualifiedName}")
                cs.visit(null, "strategy:3")
                cs.visit(null, "indy:NA")
                cs.visitEnd()
            }
            zenMetadata.visit("k", 5.toInteger())
            zenMetadata.visitEnd()
        }
    }

    private fun generateConstructor(v: ClassVisitor, name: String) {
        v.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null).let { constructor ->
            constructor.visitCode()

            val l0 = Label()
            constructor.visitLabel(l0)
            constructor.visitLineNumber(7, l0)
            constructor.visitVarInsn(Opcodes.ALOAD, 0)
            constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
            constructor.visitInsn(Opcodes.RETURN)

            val l1 = Label()
            constructor.visitLabel(l1)
            constructor.visitLocalVariable("this", "L$name;", null, l0, l1, 0)
            constructor.visitMaxs(1, 1)
            constructor.visitEnd()
        }
    }

    private fun generateMethod(v: ClassVisitor, environment: IEnvironmentMethod, method: Method) {
        this.identifyReturnType(environment, method)

        val output = AutoBoxingMethodOutput(v, Opcodes.ACC_PUBLIC, method.name, this.descriptor, null, null)

        output.visitor.visitAnnotation("Lnet/thesilkminer/mc/boson/compatibility/crafttweaker/compiler/annotation/ZenGenericFunctionMetadata;", true).let { zenFunctionMetadata ->
            zenFunctionMetadata.visit("rv", false.toBoolean())
            zenFunctionMetadata.visit("r", this.genericReturnType.name)
            zenFunctionMetadata.visit("k", 1.toInteger())
            zenFunctionMetadata.visitEnd()
        }

        val classEnvironment = EnvironmentClass(v, environment)
        val methodEnvironment = EnvironmentMethod(output, classEnvironment)

        var stackTracker = 0
        this.arguments.forEachIndexed { index, argument ->
            val typeToPut = argument.type.let { if (it == ZenType.ANY) environment.getType(method.genericParameterTypes[index]) else it }
                    ?: environment.getType(method.parameterTypes[index])
            methodEnvironment.putValue(argument.name, SymbolArgument(index + 1 + stackTracker, typeToPut), this.position)
            if (typeToPut.isLarge) ++stackTracker
        }

        output.start()
        this.statements.forEach { it.compile(methodEnvironment) }
        output.ret()
        output.end()
    }

    private fun identifyReturnType(environment: IEnvironmentMethod, method: Method) {
        val temporaryVisitor = ZenClassWriter(ClassWriter.COMPUTE_FRAMES)
        temporaryVisitor.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "throwaway", null, "java/lang/Object;", null)
        temporaryVisitor.visitSource(".dynamic.throwaway", null)

        val output = AutoBoxingMethodOutput(temporaryVisitor, Opcodes.ACC_PUBLIC, "temporary", this.descriptor, null, null)
        val classEnvironment = EnvironmentClass(temporaryVisitor, environment)
        val fakeMethodEnvironment = EnvironmentMethod(output, classEnvironment)

        var stackTracker = 0
        this.arguments.forEachIndexed { index, argument ->
            val typeToPut = argument.type.let { if (it == ZenType.ANY) environment.getType(method.genericParameterTypes[index]) else it }
                    ?: environment.getType(method.parameterTypes[index])
            fakeMethodEnvironment.putValue(argument.name, SymbolArgument(index + 1 + stackTracker, typeToPut), this.position)
            if (typeToPut.isLarge) ++stackTracker
        }

        var hasFound = false

        output.start()
        this.statements.forEach {
            it.compile(fakeMethodEnvironment)
            it.subStatements.forEach { sub ->
                if (sub is StatementReturn) {
                    val compiledExpression = sub.expression.compile(fakeMethodEnvironment, sub.returnType).eval(fakeMethodEnvironment)
                    compiledExpression.compile(true, fakeMethodEnvironment)

                    if (compiledExpression.type == ZenType.ANY) {
                        environment.error(this.position, "Found return statement, but the return type was 'any'! IAny is not yet supported")
                    }
                    this.genericReturnType = compiledExpression.type.accountForAutobox()
                    hasFound = true
                }
            }
        }
        output.end()

        if (!hasFound) {
            environment.error(position, "Unable to identify generic return type from function! Does your function have a return statement? Substituting ${this.genericReturnType}")
        }
    }

    private fun generateBridge(v: ClassVisitor, name: String, method: Method, needsSecondaryBridge: Boolean) {
        val bridge = v.visitMethod(Opcodes.ACC_PUBLIC or Opcodes.ACC_SYNTHETIC or Opcodes.ACC_BRIDGE, method.name,
                ZenTypeUtil.descriptor(method), null, null)
        bridge.visitCode()

        var maxStack = 1
        var maxLocals = 1

        val l0 = Label()
        bridge.visitLabel(l0)
        bridge.visitLineNumber(1122, l0)
        bridge.visitVarInsn(Opcodes.ALOAD, 0)

        this.arguments.forEachIndexed { index, argument ->
            ++maxStack
            ++maxLocals

            if (index in this.genericArgumentPositions) {
                // This is a generic argument, which means that we need to grab it and
                // cast it
                val indexOfArgument = this.genericArgumentPositions.indexOf(index)
                val targetGenericClass = this.genericClasses[indexOfArgument]

                bridge.visitVarInsn(Opcodes.ALOAD, index + 1) // index needs a + 1 because arguments start at 1
                bridge.visitTypeInsn(Opcodes.CHECKCAST, ZenTypeUtil.internal(targetGenericClass.java))
                return@forEachIndexed
            }

            // This isn't generic, so we just grab the parameter type and see
            val type = Type.getType(method.parameterTypes[index])
            bridge.visitVarInsn(type.getOpcode(Opcodes.ILOAD), index + 1)
            if (argument.type.isPrimitive) maxStack += this.generateUnboxingCall(bridge, argument.type, false)
        }

        bridge.visitMethodInsn(Opcodes.INVOKEVIRTUAL, name, method.name, if (needsSecondaryBridge) this.generateSecondaryBridgeDescriptor() else this.descriptor, false)
        bridge.visitInsn(Type.getReturnType(method).getOpcode(Opcodes.IRETURN))
        bridge.visitMaxs(maxStack, maxLocals)
        bridge.visitEnd()

        if (needsSecondaryBridge) this.generateSecondaryBridge(v, name, method)
    }

    // The secondary bridge is exactly the same as the first, but one of the arguments is actually a primitive
    // so we need to downcast it
    private fun generateSecondaryBridge(v: ClassVisitor, name: String, method: Method) {
        val bridge = v.visitMethod(Opcodes.ACC_PUBLIC or Opcodes.ACC_SYNTHETIC or Opcodes.ACC_BRIDGE, method.name,
                this.generateSecondaryBridgeDescriptor(), null, null)
        bridge.visitCode()

        var maxStack = 1
        var maxLocals = 1

        val l0 = Label()
        bridge.visitLabel(l0)
        bridge.visitLineNumber(1200, l0)
        bridge.visitVarInsn(Opcodes.ALOAD, 0)

        this.arguments.forEachIndexed { index, argument ->
            ++maxStack
            ++maxLocals

            if (index in this.genericArgumentPositions) {
                // This is a generic argument, which means that we need to grab it and
                // cast it
                val indexOfArgument = this.genericArgumentPositions.indexOf(index)
                val targetGenericClass = this.genericClasses[indexOfArgument]

                bridge.visitVarInsn(Opcodes.ALOAD, index + 1) // index needs a + 1 because arguments start at 1
                bridge.visitTypeInsn(Opcodes.CHECKCAST, ZenTypeUtil.internal(targetGenericClass.java))
                return@forEachIndexed
            }

            // This isn't generic, so we just grab the parameter type and see
            val type = Type.getType(method.parameterTypes[index])
            bridge.visitVarInsn(type.getOpcode(Opcodes.ILOAD), index + 1)
            if (argument.type.isPrimitive) maxStack += this.generateUnboxingCall(bridge, argument.type, true)
        }

        bridge.visitMethodInsn(Opcodes.INVOKEVIRTUAL, name, method.name, this.descriptor, false)
        bridge.visitInsn(Type.getReturnType(method).getOpcode(Opcodes.IRETURN))
        bridge.visitMaxs(maxStack, maxLocals)
        bridge.visitEnd()
    }

    private fun generateSecondaryBridgeDescriptor() = StringBuilder().apply {
        this += '('
        this@TypeConverterFunctionExpression.arguments.forEachIndexed { index, element ->
            if (element.type == ZenType.ANY) this += ZenTypeUtil.signature(this@TypeConverterFunctionExpression.interfaceClass.java.methods[0].parameterTypes[index])
            else this += element.type.accountForAutobox().signature
        }
        this += ')'
        this += ZenTypeUtil.signature(this@TypeConverterFunctionExpression.interfaceClass.java.methods[0].returnType)
    }.toString()

    private fun generateUnboxingCall(bridge: MethodVisitor, target: ZenType, shouldActuallyUnbox: Boolean): Int {
        val boxedZenType = target.accountForAutobox()
        if (boxedZenType == target) return 0

        bridge.visitTypeInsn(Opcodes.CHECKCAST, ZenTypeUtil.internal(boxedZenType.toJavaClass()))

        return if (shouldActuallyUnbox) this.generateActualUnboxing(bridge, boxedZenType) else 0
    }

    private fun generateActualUnboxing(bridge: MethodVisitor, from: ZenType): Int {
        val (methodName, returnType) = when (from) {
            ZenType.BOOLOBJECT -> Pair("booleanValue", "Z")
            ZenType.BYTEOBJECT -> Pair("byteValue", "B")
            ZenType.SHORTOBJECT -> Pair("shortValue", "S")
            ZenType.INTOBJECT -> Pair("intValue", "I")
            ZenType.LONGOBJECT -> Pair("longValue", "J")
            ZenType.FLOATOBJECT -> Pair("floatValue", "F")
            ZenType.DOUBLEOBJECT -> Pair("doubleValue", "D")
            else -> throw IllegalArgumentException("Unable to unbox from type $from")
        }

        bridge.visitMethodInsn(Opcodes.INVOKEVIRTUAL, ZenTypeUtil.internal(from.toJavaClass()), methodName, "()$returnType", false)

        return when (from) {
            ZenType.DOUBLEOBJECT, ZenType.LONGOBJECT -> 2
            else -> 1
        }
    }

    private fun Int.toInteger() = Integer(this)
    private fun Boolean.toBoolean() = java.lang.Boolean(this)

    private val ZenType.isPrimitive get() = when (this) {
        ZenType.BOOL, ZenType.BYTE, ZenType.SHORT, ZenType.INT, ZenType.LONG, ZenType.FLOAT, ZenType.DOUBLE -> true
        else -> false
    }

    private fun ZenType.accountForAutobox() = when (this) {
        ZenType.BOOL -> ZenType.BOOLOBJECT
        ZenType.BYTE -> ZenType.BYTEOBJECT
        ZenType.SHORT -> ZenType.SHORTOBJECT
        ZenType.INT -> ZenType.INTOBJECT
        ZenType.LONG -> ZenType.LONGOBJECT
        ZenType.FLOAT -> ZenType.FLOATOBJECT
        ZenType.DOUBLE -> ZenType.DOUBLEOBJECT
        else -> this
    }

    private val StatementReturn.returnType get() = this::class.java.getDeclaredField("returnType").apply { this.isAccessible = true }.get(this).uncheckedCast<ZenType>()
}
