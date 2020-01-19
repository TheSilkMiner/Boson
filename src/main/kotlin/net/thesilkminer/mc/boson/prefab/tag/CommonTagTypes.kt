@file:JvmName("CTT")

package net.thesilkminer.mc.boson.prefab.tag

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.tag.TagType

private typealias NPE = KotlinNullPointerException

// TODO("Don't depend on MC, but only on the API")
// TODO("Of course this implies that Block, Item, etc... are actually wrapped into their own types in the API")
val blockTagType by lazy { TagType(IBlockState::class, "blocks") { it.findBlockState() } }
val fluidTagType by lazy { TagType(Fluid::class, "fluids") { FluidRegistry.getFluid(it.path).n(it, "fluids") } } //TODO("Actually implement fluids correctly")
val itemTagType by lazy { TagType(ItemStack::class, "items") { it.findItemStack() } }

private fun NameSpacedString.toResourceLocation() = ResourceLocation(this.nameSpace, this.path)
private fun <T> T?.n(a: NameSpacedString, t: String) = this ?: throw IllegalArgumentException("Tags of type '$t' don't support null entries, but '$a' was", NPE("null"))

private fun NameSpacedString.findBlockState() = ForgeRegistries.BLOCKS.getValue(this.toResourceLocation()).n(this, "blocks").defaultState
private fun NameSpacedString.findItemStack() = ItemStack(ForgeRegistries.ITEMS.getValue(this.toResourceLocation()).n(this, "items"), 1, 0)
