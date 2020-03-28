/*
 * Copyright (C) 2020  TheSilkMiner
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

package net.thesilkminer.mc.boson.implementation.tag

import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.api.tag.TagType
import java.lang.RuntimeException

internal class BosonTag<T : Any>(override val name: NameSpacedString, override val type: TagType<T>) : Tag<T> {
    private val mutableElements = mutableSetOf<T>()
    private val mutableOtherTags = mutableSetOf<Tag<out T>>()

    private val isFrozen get() = bosonApi.tagRegistry.isFrozen

    private var statefulGetterLock = false

    override val elements: Set<T>
        get() = mutableSetOf<T>().apply {
            if (this@BosonTag.statefulGetterLock) throw CircularTagDependencyException(this@BosonTag.name)
            this@BosonTag.statefulGetterLock = true
            this.addAllWithCheck(this@BosonTag.mutableElements)
            this@BosonTag.mutableOtherTags.forEach { this.addAllWithCheck(it.elements) }
            this@BosonTag.statefulGetterLock = false
        }.toSet()

    override fun add(elements: Set<T>) {
        if (this.isFrozen) return
        this.mutableElements.addAll(elements)
    }

    override fun addFrom(other: Tag<out T>) {
        if (this.isFrozen) return
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
        if (this.isFrozen) return
        this.mutableElements.removeAll(elements)
    }

    override fun removeFrom(other: Tag<out T>) {
        if (this.isFrozen) return
        this.mutableOtherTags.remove(other)
    }

    override fun clear() {
        if (this.isFrozen) return
        this.mutableElements.clear()
        this.mutableOtherTags.clear()
    }

    private fun MutableSet<T>.addAllWithCheck(elements: Collection<T>) = elements.forEach { target ->
        if (this.none { this@BosonTag.type.equalityEvaluator(target, it) }) this += target
    }
}

private class CircularTagDependencyException(name: NameSpacedString) : RuntimeException("The tag $name specifies a circular dependency on itself: this cannot be resolved")

