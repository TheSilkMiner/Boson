package net.thesilkminer.mc.boson.api.tag

import net.thesilkminer.mc.boson.api.id.NameSpacedString

interface Tag<T : Any> {
    val name: NameSpacedString
    val type: TagType<T>

    val elements: Set<T>

    fun add(elements: Set<T>)
    fun addFrom(other: Tag<out T>)
    fun replace(elements: Set<T>)
    fun replaceWith(other: Tag<out T>)
    fun remove(elements: Set<T>)
    fun removeFrom(other: Tag<out T>)
    fun clear()

    fun add(vararg elements: NameSpacedString) = this.add(elements.map { this.type.toElement(it) }.toSet())
    fun replace(vararg elements: NameSpacedString) = this.replace(elements.map { this.type.toElement(it) }.toSet())
    fun remove(vararg elements: NameSpacedString) = this.remove(elements.map { this.type.toElement(it) }.toSet())

    operator fun contains(element: T) = element in this.elements
    operator fun plusAssign(element: T) = this.add(setOf(element))
    operator fun plusAssign(elements: Set<T>) = this.add(elements)
    operator fun plusAssign(element: NameSpacedString) = this.add(element)
    operator fun plusAssign(other: Tag<out T>) = this.addFrom(other)
    operator fun minusAssign(element: T) = this.remove(setOf(element))
    operator fun minusAssign(elements: Set<T>) = this.remove(elements)
    operator fun minusAssign(element: NameSpacedString) = this.remove(element)
    operator fun minusAssign(other: Tag<out T>) = this.removeFrom(other)
    operator fun unaryMinus() = this.clear()
}
