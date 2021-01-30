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

package net.thesilkminer.mc.boson.api.loader

interface Context {
    operator fun <T : Any> get(key: ContextKey<out T>): T?
    operator fun <T : Any> set(key: ContextKey<out T>, value: T)
    fun <T : Any> computeIfAbsent(key: ContextKey<out T>, supplier: (ContextKey<*>) -> T): T
    fun <T : Any, R> ifPresent(key: ContextKey<out T>, consumer: (T) -> R): R?
}
