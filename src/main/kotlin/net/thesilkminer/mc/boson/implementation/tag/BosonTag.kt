package net.thesilkminer.mc.boson.implementation.tag

import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.api.tag.TagType

class BosonTag<T : Any>(override val name: NameSpacedString, override val type: TagType<T>) : Tag<T> {
    private val mutableElements = mutableSetOf<T>()

    override val elements: Set<T> = mutableElements.toSet() // Copies

    override fun add(elements: Set<T>) {
        this.mutableElements.addAll(elements)
    }

    override fun addFrom(other: Tag<out T>) {
        this.add(other.elements)
    }

    override fun replace(elements: Set<T>) {
        this.clear()
        this.add(elements)
    }

    override fun replaceWith(other: Tag<out T>) {
        this.replace(other.elements)
    }

    override fun remove(elements: Set<T>) {
        this.mutableElements.removeAll(elements)
    }

    override fun removeFrom(other: Tag<out T>) {
        this.remove(other.elements)
    }

    override fun clear() {
        this.mutableElements.clear()
    }
}
