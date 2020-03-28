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

package net.thesilkminer.mc.boson.prefab.communication

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import kotlin.reflect.KClass

class StringMessage(sender: String, key: String, override val content: String) : BasicMessage<String>(sender, String::class, key)
class NameSpacedStringMessage(sender: String, key: String, override val content: NameSpacedString) : BasicMessage<NameSpacedString>(sender, NameSpacedString::class, key)
class ItemStackMessage(sender: String, key: String, override val content: ItemStack) : BasicMessage<ItemStack>(sender, ItemStack::class, key)
class CompoundNbtMessage(sender: String, key: String, override val content: NBTTagCompound) : BasicMessage<NBTTagCompound>(sender, NBTTagCompound::class, key) // TODO("API this out")
class FunctionMessage(sender: String, key: String, override val content: KClass<*>) : BasicMessage<KClass<*>>(sender, KClass::class, key)
class PairMessage<A, B>(sender: String, key: String, override val content: Pair<A, B>) : BasicMessage<Pair<A, B>>(sender, Pair::class.uncheckedCast(), key)
class ListMessage<T>(sender: String, key: String, override val content: List<T>) : BasicMessage<List<T>>(sender, List::class.uncheckedCast(), key)
//TODO("PositionMessage")
