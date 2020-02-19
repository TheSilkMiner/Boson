package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.sequence

import net.thesilkminer.kotlin.commons.lang.plusAssign
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import stanhebben.zenscript.compiler.EnvironmentClass
import stanhebben.zenscript.compiler.EnvironmentMethod
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.compiler.ZenClassWriter
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionJavaLambdaSimpleGeneric
import stanhebben.zenscript.symbols.SymbolArgument
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.util.MethodOutput
import stanhebben.zenscript.util.ZenTypeUtil
import java.lang.reflect.Method

class SimpleGenericLambdaExpression(private val wrapped: ExpressionJavaLambdaSimpleGeneric) : Expression(wrapped.position) {
    private val interfaceClass get() = wrapped.interfaceClass.kotlin
    private val genericClass get() = wrapped.genericClass.kotlin
    private val arguments get() = wrapped.arguments
    private val statements get() = wrapped.statements
    private val descriptor get() = wrapped.descriptor
    private val type @JvmName("getWrappedType") get() = wrapped.type

    override fun getType(): ZenType = this.type

    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        if (!result) return
        if (environment == null) return

        val method = this.interfaceClass.java.methods[0]
        val className = ("${this.position.file.className}\$sam\$${this.interfaceClass.simpleName?.replace('.', '_') ?: "unknown"}" +
                "\$${TypeConverterFunctionExpression.getAndIncrement(this.interfaceClass)}").replace(';', '$')
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
        v.visitSource(".dynamic.SimpleGenericLambdaExpression.asm", null)

        this.generateAnnotations(v, name)

        this.generateConstructor(v, name)
        this.generateMethod(v, environment, method)

        if (this.genericClass != Object::class) {
            this.generateBridge(v, name, method)
        }
    }

    private fun generateMethodSignature() = StringBuilder().apply {
        this += "Ljava/lang/Object;"
        this += ZenTypeUtil.signature(this@SimpleGenericLambdaExpression.interfaceClass.java)
        this.deleteCharAt(this.count() - 1)
        this += '<'
        this += ZenTypeUtil.signature(this@SimpleGenericLambdaExpression.genericClass.java)
        this += ">;"
    }.toString()

    private fun generateAnnotations(v: ClassVisitor, name: String) {
        v.visitAnnotation("Lkotlin/Metadata;", true).let { kotlinMetadata ->
            kotlinMetadata.visit("k", 1.toInteger())
            kotlinMetadata.visit("mv", intArrayOf(1, 1, 16))
            kotlinMetadata.visit("bv", intArrayOf(1, 0, 3))
            kotlinMetadata.visitArray("d1").let { d1 ->
                d1.visit(null, "")
                d1.visitEnd()
            }
            kotlinMetadata.visitArray("d2").let { d2 ->
                d2.visit(null, "L$name;")
                d2.visitEnd()
            }
            kotlinMetadata.visit("xs", "")
            kotlinMetadata.visit("pn", "")
            kotlinMetadata.visit("xi", 0b0000.toInteger())
            kotlinMetadata.visitEnd()
        }
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
        val output = MethodOutput(v, Opcodes.ACC_PUBLIC, method.name, this.descriptor, null, null)

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

    private fun generateBridge(v: ClassVisitor, name: String, method: Method) {
        val bridge = v.visitMethod(Opcodes.ACC_PUBLIC or Opcodes.ACC_SYNTHETIC or Opcodes.ACC_BRIDGE, method.name,
                ZenTypeUtil.descriptor(method), null, null)
        bridge.visitCode()

        var maxStack = 2
        var maxLocals = 2

        val l0 = Label()
        bridge.visitLabel(l0)
        bridge.visitLineNumber(1122, l0)
        bridge.visitVarInsn(Opcodes.ALOAD, 0)
        bridge.visitVarInsn(Opcodes.ALOAD, 1)
        bridge.visitTypeInsn(Opcodes.CHECKCAST, ZenTypeUtil.internal(this.genericClass.java))

        // TODO("Kotlin-ify")
        if (this.arguments.count() > 1) {
            for (i in 1 until this.arguments.count()) {
                ++maxStack
                ++maxLocals
                bridge.visitVarInsn(Type.getType(method.parameterTypes[i]).getOpcode(Opcodes.ILOAD), i + 1)
            }
        }

        bridge.visitMethodInsn(Opcodes.INVOKEVIRTUAL, name, method.name, this.descriptor, false)
        bridge.visitInsn(Type.getReturnType(method).getOpcode(Opcodes.IRETURN))
        bridge.visitMaxs(maxStack, maxLocals)
        bridge.visitEnd()
    }

    private fun Int.toInteger() = Integer(this)
    private fun Boolean.toBoolean() = java.lang.Boolean(this)
}
