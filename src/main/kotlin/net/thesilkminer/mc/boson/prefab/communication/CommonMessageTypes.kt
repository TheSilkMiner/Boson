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
