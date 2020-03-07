package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.sequence

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Type
import stanhebben.zenscript.util.MethodOutput
import kotlin.reflect.KClass

internal class AutoBoxingMethodOutput(cls: ClassVisitor, access: Int, name: String, descriptor: String?, signature: String?, exceptions: Array<String>?)
    : MethodOutput(cls, access, name, descriptor, signature, exceptions) {

    private val autoBoxingCalls = mutableMapOf<Int, () -> Unit>().apply { this.populateMap() }.toMap()

    override fun returnType(type: Type?) {
        if (!(type ?: return).isPrimitive) {
            super.returnType(type)
            return
        }
        this.generateAutoBoxingCall(type)
    }

    private fun generateAutoBoxingCall(type: Type) = this.autoBoxingCalls[type.sort]?.let { it() } ?: throw IllegalStateException("Unable to autobox type $type")

    private fun MutableMap<Int, () -> Unit>.populateMap() {
        this[Type.BOOLEAN] = { this@AutoBoxingMethodOutput.valueOf(java.lang.Boolean::class, java.lang.Boolean.TYPE.kotlin) }
        this[Type.CHAR] = { this@AutoBoxingMethodOutput.valueOf(Character::class, Character.TYPE.kotlin) }
        this[Type.BYTE] = { this@AutoBoxingMethodOutput.valueOf(java.lang.Byte::class, java.lang.Byte.TYPE.kotlin) }
        this[Type.SHORT] = { this@AutoBoxingMethodOutput.valueOf(java.lang.Short::class, java.lang.Short.TYPE.kotlin) }
        this[Type.INT] = { this@AutoBoxingMethodOutput.valueOf(Integer::class, Integer.TYPE.kotlin) }
        this[Type.FLOAT] = { this@AutoBoxingMethodOutput.valueOf(java.lang.Float::class, java.lang.Float.TYPE.kotlin) }
        this[Type.LONG] = { this@AutoBoxingMethodOutput.valueOf(java.lang.Long::class, java.lang.Long.TYPE.kotlin) }
        this[Type.DOUBLE] = { this@AutoBoxingMethodOutput.valueOf(java.lang.Double::class, java.lang.Double.TYPE.kotlin) }
    }

    private fun valueOf(targetClass: KClass<*>, primitiveType: KClass<*>) {
        super.invokeStatic(targetClass.java, "valueOf", targetClass.java, primitiveType.java)
        super.returnObject()
    }

    private val Type.isPrimitive get() = when (this.sort) {
        Type.VOID, Type.ARRAY, Type.OBJECT -> false
        Type.BOOLEAN, Type.CHAR, Type.BYTE, Type.SHORT, Type.INT, Type.FLOAT, Type.LONG, Type.DOUBLE -> true
        Type.METHOD -> throw IllegalArgumentException("Unable to return a Type.METHOD")
        else -> throw IllegalStateException("Type sort ${this.sort} is not a valid sort! How did you get this even? $this")
    }
}
