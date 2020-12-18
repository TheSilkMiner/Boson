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
import net.thesilkminer.mc.boson.api.tag.CircularTagReferenceException
import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.api.tag.TagType
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal class BosonTag<T : Any>(override val name: NameSpacedString, override val type: TagType<T>) : Tag<T> {
    private val reentrantLock = ReentrantLock()

    private val mutableElements = mutableSetOf<T>()
    private val mutableOtherTags = mutableSetOf<Tag<out T>>()

    private var statefulGetterLock = false
    private val elementsCache by lazy(this::gatherElements)

    private val isFrozen get() = bosonApi.tagRegistry.isFrozen

    override val elements: Set<T> get() = if (this.isFrozen) this.elementsCache else this.gatherElements()

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

    private fun gatherElements(): Set<T> {
        val set = mutableSetOf<T>()
        this.reentrantLock.withLock {
            if (this.statefulGetterLock) throw CircularTagReferenceException(this)
            this.statefulGetterLock = true
            set.addAllWithCheck(this.mutableElements, this.type)
            this.mutableOtherTags.forEach { set.addAllWithCheck(it.elements, this.type) }
            this.statefulGetterLock = false
        }
        return set.toSet()
    }

    private fun MutableSet<T>.addAllWithCheck(elements: Collection<T>, type: TagType<T>) = elements.forEach { target ->
        if (this.none { type.equalityEvaluator(target, it) }) this += target
    }
}

