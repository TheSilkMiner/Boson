@file:JvmName("CTT")

package net.thesilkminer.mc.boson.prefab.tag

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.thesilkminer.mc.boson.api.tag.TagType

val blockTagType get() = TagType.find<IBlockState>("blocks") ?: throw NoSuchElementException("Tag type for 'blocks' wasn't registered")
val fluidTagType get() = TagType.find<Fluid>("fluids") ?: throw NoSuchElementException("Tag type for 'fluids' wasn't registered")
val itemTagType get() = TagType.find<ItemStack>("items") ?: throw NoSuchElementException("Tag type for 'items' wasn't registered")
