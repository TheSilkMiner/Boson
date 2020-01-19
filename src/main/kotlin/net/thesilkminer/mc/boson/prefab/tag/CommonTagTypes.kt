@file:JvmName("CTT")

package net.thesilkminer.mc.boson.prefab.tag

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.tag.TagType

private typealias NPE = KotlinNullPointerException

// TODO("Don't depend on MC, but only on the API")
// TODO("Of course this implies that Block, Item, etc... are actually wrapped into their own types in the API")
val blockTagType by lazy { TagType(Block::class, "blocks") { ForgeRegistries.BLOCKS.getValue(it.toResourceLocation()).n(it, "blocks") } }
val fluidTagType by lazy { TagType(Fluid::class, "fluids") { FluidRegistry.getFluid(it.path).n(it, "fluids") } } //TODO("Actually implement fluids correctly")
val itemTagType by lazy { TagType(Item::class, "items") { ForgeRegistries.ITEMS.getValue(it.toResourceLocation()).n(it, "items") } }

private fun NameSpacedString.toResourceLocation() = ResourceLocation(this.nameSpace, this.path)
private fun <T> T?.n(a: NameSpacedString, t: String) = this ?: throw IllegalArgumentException("Tags of type '$t' don't support null entries, but '$a' was", NPE("null"))
