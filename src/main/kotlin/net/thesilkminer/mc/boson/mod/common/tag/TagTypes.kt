package net.thesilkminer.mc.boson.mod.common.tag

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.tag.TagType
import net.thesilkminer.mc.boson.prefab.naming.toResourceLocation

private typealias NPE = KotlinNullPointerException

val blocks by lazy { TagType(IBlockState::class, "blocks") { it.findBlockState() } }
val fluids by lazy { TagType(Fluid::class, "fluids") { FluidRegistry.getFluid(it.path).n(it, "fluids") } } //TODO("Actually implement fluids correctly")
val items by lazy { TagType(ItemStack::class, "items") { it.findItemStack() } }

private fun NameSpacedString.findBlockState() = ForgeRegistries.BLOCKS.getValue(this.toResourceLocation()).n(this, "blocks").defaultState
private fun NameSpacedString.findItemStack() = ItemStack(ForgeRegistries.ITEMS.getValue(this.toResourceLocation()).n(this, "items"), 1, 0)

private fun <T> T?.n(a: NameSpacedString, t: String) = this ?: throw IllegalArgumentException("Tags of type '$t' don't support null entries, but '$a' was", NPE("null"))
