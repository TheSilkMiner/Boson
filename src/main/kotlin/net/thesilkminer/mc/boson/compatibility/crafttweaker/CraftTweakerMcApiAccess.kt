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

@file:JvmName("CTMCAA")

package net.thesilkminer.mc.boson.compatibility.crafttweaker

import crafttweaker.api.block.IBlock
import crafttweaker.api.block.IBlockDefinition
import crafttweaker.api.block.IBlockState
import crafttweaker.api.block.IMaterial
import crafttweaker.api.command.ICommand
import crafttweaker.api.command.ICommandSender
import crafttweaker.api.container.IContainer
import crafttweaker.api.creativetabs.ICreativeTab
import crafttweaker.api.damage.IDamageSource
import crafttweaker.api.data.IData
import crafttweaker.api.entity.IEntity
import crafttweaker.api.entity.IEntityAgeable
import crafttweaker.api.entity.IEntityAnimal
import crafttweaker.api.entity.IEntityCreature
import crafttweaker.api.entity.IEntityEquipmentSlot
import crafttweaker.api.entity.IEntityItem
import crafttweaker.api.entity.IEntityLiving
import crafttweaker.api.entity.IEntityLivingBase
import crafttweaker.api.entity.IEntityMob
import crafttweaker.api.entity.IEntityXp
import crafttweaker.api.game.ITeam
import crafttweaker.api.item.IIngredient
import crafttweaker.api.item.IItemStack
import crafttweaker.api.item.WeightedItemStack
import crafttweaker.api.liquid.ILiquidDefinition
import crafttweaker.api.liquid.ILiquidStack
import crafttweaker.api.oredict.IOreDictEntry
import crafttweaker.api.player.IPlayer
import crafttweaker.api.potions.IPotion
import crafttweaker.api.potions.IPotionEffect
import crafttweaker.api.server.IServer
import crafttweaker.api.world.IBiome
import crafttweaker.api.world.IBlockPos
import crafttweaker.api.world.IFacing
import crafttweaker.api.world.IRayTraceResult
import crafttweaker.api.world.IVector3d
import crafttweaker.api.world.IWorld
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityAgeable
import net.minecraft.entity.EntityCreature
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.item.EntityXPOrb
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.scoreboard.Team
import net.minecraft.server.MinecraftServer
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.OreIngredient
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import java.lang.reflect.Method
import kotlin.reflect.KClass

private val targetClass by lazy { Class.forName("crafttweaker.api.minecraft.CraftTweakerMC").kotlin }
private val methodCache = mutableMapOf<String, Method>()

private fun findMethod(name: String, vararg arguments: KClass<*>): Method {
    val methodString = "$name(${arguments.joinToString { it.simpleName ?: it.qualifiedName ?: it.java.name ?: "<UNKNOWN>" }})"
    return methodCache.computeIfAbsent(methodString) {
        try {
            targetClass.java.getDeclaredMethod(name, *arguments.map { arg -> arg.java }.toTypedArray())
        } catch (e: NoSuchMethodException) {
            throw IllegalArgumentException("No CraftTweaker API method found for key $it", e)
        }
    }
}

private inline fun <reified T> Method.call(vararg arguments: Any?) = this.invoke(null, *arguments).uncheckedCast<T>()

// This isn't really API but we expose it to other modules so there is no need to copy this mess
fun IItemStack?.toNativeStack() = findMethod("getItemStack", IItemStack::class).call<ItemStack>(this)
fun IIngredient?.toNativeStack() = findMethod("getItemStack", IIngredient::class).call<ItemStack>(this)
fun ItemStack.toZen(wildcard: Boolean = false) = findMethod(if (wildcard) "getIItemStackWildcardSize" else "getIItemStack", ItemStack::class)
        .call<IItemStack?>(this)
fun Item.toZen(amount: Int = 1, meta: Int = OreDictionary.WILDCARD_VALUE) = ItemStack(this, amount, meta).toZen(wildcard = meta == OreDictionary.WILDCARD_VALUE)
fun String.toZenOreDictionaryEntry() = findMethod("getOreDict", String::class).call<IOreDictEntry>(this)
fun EntityPlayer?.toZen() = findMethod("getIPlayer", EntityPlayer::class).call<IPlayer?>(this)
fun IPlayer?.toNative() = findMethod("getPlayer", IPlayer::class).call<EntityPlayer?>(this)
fun NBTBase?.toZen(canModify: Boolean = false) = findMethod(if (canModify) "getIDataModifyable" else "getIData", NBTBase::class).call<IData?>(this)
fun IData?.toNative() = findMethod("getNBT", IData::class).call<NBTBase?>(this)
fun IData?.toNativeCompound() = findMethod("getNBT", IData::class).call<NBTTagCompound?>(this)
fun IBlockAccess.findZenBlockAt(x: Int, y: Int, z: Int) = findMethod("getBlock", IBlockAccess::class, Int::class, Int::class, Int::class)
        .call<IBlock>(this, x, y, z)
fun Block.toZenDefinition() = findMethod("getBlockDefinition", Block::class).call<IBlockDefinition>(this)
fun Block?.toZen(meta: Int = OreDictionary.WILDCARD_VALUE) = findMethod("getBlock", Block::class, Int::class).call<IBlock?>(this, meta)
fun IItemStack?.toNativeBlock() = findMethod("getBlock", IItemStack::class).call<Block?>(this)
fun IBlock?.toNative() = findMethod("getBlock", IBlock::class).call<Block?>(this)
fun IBlockDefinition.toNativeBlock() = findMethod("getBlock", IBlockDefinition::class).call<Block>(this)
fun ILiquidStack?.toNative() = findMethod("getLiquidStack", ILiquidStack::class).call<FluidStack>(this)
fun FluidStack?.toZen() = findMethod("getILiquidStack", FluidStack::class).call<ILiquidStack>(this)
fun List<*>.toZenOreDictionaryEntry() = findMethod("getOreDictEntryFromArray", List::class).call<IOreDictEntry>(this)
fun Any?.toZenIngredient() = findMethod("getIIngredient", Object::class).call<IIngredient?>(this)
fun IIngredient?.toNative() = findMethod("getIngredient", IIngredient::class).call<Ingredient>(this)
fun OreIngredient.toZenEntry() = findMethod("getOreDict", OreIngredient::class).call<IOreDictEntry>(this)
fun World?.toZen() = findMethod("getIWorld", World::class).call<IWorld?>(this)
fun IWorld?.toNative() = findMethod("getWorld", IWorld::class).call<World?>(this)
fun IItemStack.matches(stack: IItemStack, withWildcard: Boolean = true) = findMethod("matches", IItemStack::class, ItemStack::class, Boolean::class)
        .call<Boolean>(this, stack, withWildcard)
fun IItemStack.matchesExactly(stack: IItemStack) = findMethod("matchesExact", IItemStack::class, ItemStack::class).call<Boolean>(this, stack)
fun net.minecraft.block.state.IBlockState?.toZen() = findMethod("getBlockState", net.minecraft.block.state.IBlockState::class).call<IBlockState?>(this)
fun IBlockState?.toNative() = findMethod("getBlockState", IBlockState::class).call<net.minecraft.block.state.IBlockState?>(this)
fun Entity?.toZen() = findMethod("getIEntity", Entity::class).call<IEntity?>(this)
fun EntityXPOrb?.toZen() = findMethod("getIEntityXp", EntityXPOrb::class).call<IEntityXp?>(this)
fun EntityItem?.toZen() = findMethod("getIEntityItem", EntityItem::class).call<IEntityItem?>(this)
fun EntityLivingBase?.toZen() = findMethod("getIEntityLivingBase", EntityLivingBase::class).call<IEntityLivingBase?>(this)
fun EntityLiving?.toZen() = findMethod("getIEntityLiving", EntityLiving::class).call<IEntityLiving?>(this)
fun EntityCreature?.toZen() = findMethod("getIEntityCreature", EntityCreature::class).call<IEntityCreature?>(this)
fun EntityAgeable?.toZen() = findMethod("getIEntityAgeable", EntityAgeable::class).call<IEntityAgeable?>(this)
fun EntityAnimal?.toZen() = findMethod("getIEntityAnimal", EntityAnimal::class).call<IEntityAnimal?>(this)
fun EntityMob?.toZen() = findMethod("getIEntityMob", EntityMob::class).call<IEntityMob?>(this)
fun BlockPos?.toZen() = findMethod("getIBlockPos", BlockPos::class).call<IBlockPos?>(this)
fun IBlockPos?.toNative() = findMethod("getBlockPos", IBlockPos::class).call<IBlockPos?>(this)
fun Team?.toZen() = findMethod("getITeam", Team::class).call<ITeam?>(this)
fun ITeam?.toNative() = findMethod("getTeam", ITeam::class).call<Team?>(this)
fun DamageSource?.toZen() = findMethod("getIDamageSource", DamageSource::class).call<IDamageSource?>(this)
fun IDamageSource?.toNative() = findMethod("getDamageSource", IDamageSource::class).call<DamageSource?>(this)
fun Material?.toZen() = findMethod("getIMaterial", Material::class).call<IMaterial?>(this)
fun IMaterial?.toNative() = findMethod("getMaterial", IMaterial::class).call<Material?>(this)
fun IEntityAnimal?.toNative() = findMethod("getEntityAnimal", IEntityAnimal::class).call<EntityAnimal?>(this)
fun EntityEquipmentSlot?.toZen() = findMethod("getIEntityEquipmentSlot", EntityEquipmentSlot::class).call<IEntityEquipmentSlot?>(this)
fun IEntityEquipmentSlot?.toNative() = findMethod("getEntityEquipmentSlot", IEntityEquipmentSlot::class).call<EntityEquipmentSlot?>(this)
fun IEntityLivingBase?.toNative() = findMethod("getEntityLivingBase", IEntityLivingBase::class).call<EntityLivingBase?>(this)
fun Potion?.toZen() = findMethod("getIPotion", Potion::class).call<IPotion?>(this)
fun IPotion?.toNative() = findMethod("getPotion", IPotion::class).call<Potion?>(this)
fun PotionEffect?.toZen() = findMethod("getIPotionEffect", PotionEffect::class).call<IPotionEffect?>(this)
fun IPotionEffect?.toNative() = findMethod("getPotionEffect", IPotionEffect::class).call<PotionEffect?>(this)
fun List<Ingredient>.toZen() = findMethod("getIIngredients", List::class).call<Array<IIngredient?>>(this)
fun IEntityItem?.toNative() = findMethod("getEntityItem", IEntityItem::class).call<EntityItem?>(this)
fun RayTraceResult?.toZen() = findMethod("getIRayTraceResult", RayTraceResult::class).call<IRayTraceResult?>(this)
fun IRayTraceResult?.toNative() = findMethod("getRayTraceResult", IRayTraceResult::class).call<RayTraceResult?>(this)
fun Container?.toZen() = findMethod("getIContainer", Container::class).call<IContainer?>(this)
fun IContainer?.toNative() = findMethod("getContainer", IContainer::class).call<Container?>(this)
fun EnumFacing?.toZen() = findMethod("getIFacing", EnumFacing::class).call<IFacing?>(this)
fun ICreativeTab?.toNative() = findMethod("getCreativeTabs", ICreativeTab::class).call<CreativeTabs?>(this)
fun ICommandSender?.toNative() = findMethod("getICommandSender", ICommandSender::class).call<net.minecraft.command.ICommandSender?>(this)
fun net.minecraft.command.ICommandSender?.toZen() = findMethod("getICommandSender", net.minecraft.command.ICommandSender::class)
        .call<ICommandSender?>(this)
fun net.minecraft.command.ICommand?.toZen() = findMethod("getICommand", net.minecraft.command.ICommand::class).call<ICommand?>(this)
fun ICommand?.toNative() = findMethod("getICommand", ICommand::class).call<net.minecraft.command.ICommand?>(this)
fun IServer?.toNative() = findMethod("getMCServer", IServer::class).call<MinecraftServer?>(this)
fun IVector3d?.toNative() = findMethod("getVec3d", IVector3d::class).call<Vec3d?>(this)
fun Vec3d?.toZen() = findMethod("getIVector3d", Vec3d::class).call<IVector3d?>(this)
fun List<ItemStack>.toZenWeightedList() = findMethod("getWeightedItemStackList", List::class).call<List<WeightedItemStack>>(this)
fun ILiquidDefinition?.toNative() = findMethod("getFluid", ILiquidDefinition::class).call<Fluid?>(this)
fun Fluid?.toZen() = findMethod("getILiquidDefinition", Fluid::class).call<ILiquidDefinition?>(this)
fun IBiome?.toNative() = findMethod("getBiome", IBiome::class).call<Biome?>(this)

val IIngredient.examples get() = findMethod("getExamples", IIngredient::class).call<Array<ItemStack>>(this)

fun mergeIIngredients(vararg ingredients: IIngredient) = findMethod("mergeIngredients", Array<IIngredient>::class).call<IIngredient>(*ingredients)
fun getZenWorldFromId(id: Int) = findMethod("getWorldByID", Int::class).call<IWorld>(id)
