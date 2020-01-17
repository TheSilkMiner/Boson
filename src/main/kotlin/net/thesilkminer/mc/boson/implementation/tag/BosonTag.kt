package net.thesilkminer.mc.boson.implementation.tag

import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.api.tag.TagType
import java.lang.RuntimeException

class BosonTag<T : Any>(override val name: NameSpacedString, override val type: TagType<T>) : Tag<T> {
    private val mutableElements = mutableSetOf<T>()
    private val mutableOtherTags = mutableSetOf<Tag<out T>>()

    private var statefulGetterLock = false

    override val elements: Set<T>
        get() = mutableSetOf<T>().apply {
            if (this@BosonTag.statefulGetterLock) throw CircularTagDependencyException(this@BosonTag.name)
            this@BosonTag.statefulGetterLock = true
            this.addAll(this@BosonTag.mutableElements)
            this@BosonTag.mutableOtherTags.forEach { this.addAll(it.elements) }
            this@BosonTag.statefulGetterLock = false
        }.toSet()

    override fun add(elements: Set<T>) {
        this.mutableElements.addAll(elements)
    }

    override fun addFrom(other: Tag<out T>) {
        this.mutableOtherTags += other
        this.elements // Fail fast in case of circular dependencies
    }

    override fun replace(elements: Set<T>) {
        this.clear()
        this.add(elements)
    }

    override fun replaceWith(other: Tag<out T>) {
        this.clear()
        this.addFrom(other)
    }

    override fun remove(elements: Set<T>) {
        this.mutableElements.removeAll(elements)
    }

    override fun removeFrom(other: Tag<out T>) {
        this.mutableOtherTags.remove(other)
    }

    override fun clear() {
        this.mutableElements.clear()
        this.mutableOtherTags.clear()
    }
}

private class CircularTagDependencyException(name: NameSpacedString) : RuntimeException("The tag $name specifies a circular dependency on itself: this cannot be resolved")

