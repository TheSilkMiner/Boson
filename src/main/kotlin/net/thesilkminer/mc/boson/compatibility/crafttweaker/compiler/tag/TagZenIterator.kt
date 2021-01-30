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

package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag

import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTag
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import stanhebben.zenscript.type.IZenIterator
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.util.MethodOutput

internal class TagZenIterator(private val methodOutput: MethodOutput?, private val targetType: TagZenType) : IZenIterator {
    private var iterator: Int? = null

    override fun compileStart(locals: IntArray?) {
        this.methodOutput?.let {
            this.iterator = it.local(Type.getType(Iterator::class.java))
            it.invokeVirtual(ZenTag::class.java, "getElements", List::class.java)
            it.invoke(Iterable::class.java, "iterator", Iterator::class.java)
            it.storeObject(this.iterator!!)
        }
    }

    override fun compilePreIterate(locals: IntArray?, exit: Label?) {
        this.methodOutput?.let {
            it.loadObject(this.iterator!!)
            it.invokeInterface(Iterator::class.java, "hasNext", java.lang.Boolean.TYPE)
            it.ifEQ(exit)

            it.loadObject(this.iterator!!)
            it.invokeInterface(Iterator::class.java, "next", Object::class.java)
            it.checkCast(this.targetType.genericType.toASMType().internalName)
            it.store(this.targetType.genericType.toASMType(), locals!![0])
        }
    }

    override fun compilePostIterate(locals: IntArray?, exit: Label?, repeat: Label?) {
        this.methodOutput?.goTo(repeat)
    }

    override fun compileEnd() {
        this.methodOutput?.visitor?.visitInsn(Opcodes.NOP)
    }

    override fun getType(i: Int): ZenType = this.targetType.genericType
}
