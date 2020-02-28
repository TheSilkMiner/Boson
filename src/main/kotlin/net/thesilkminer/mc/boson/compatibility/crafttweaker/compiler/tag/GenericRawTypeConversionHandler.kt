@file:JvmName("GRTCH")

package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import crafttweaker.api.block.IBlockState
import crafttweaker.api.item.IItemStack
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.api.tag.TagType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTag
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toNative
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toNativeStack
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toZen
import net.thesilkminer.mc.boson.prefab.tag.blockTagType
import net.thesilkminer.mc.boson.prefab.tag.itemTagType
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.util.ZenPosition
import kotlin.reflect.KClass

// KClass <-> KClass
private val converterBiMap = HashBiMap.create<KClass<*>, KClass<*>>().apply { this.populate() }

@JvmName("populate\$KClass\$KClass")
private fun BiMap<KClass<*>, KClass<*>>.populate() {
    this[IItemStack::class] = ItemStack::class
    this[IBlockState::class] = net.minecraft.block.state.IBlockState::class
    // TODO("fluids")
    // TODO("everything else that may require custom handling")
}

fun KClass<*>.convertToZenGenericType() = converterBiMap.inverse()[this] ?: this.warn()
fun KClass<*>.convertToNativeGenericType() = converterBiMap[this] ?: this.warn()

private fun KClass<*>.warn() = this/*.also { l.info("Unable to map '$this': returning the same data and hoping") }*/

// Type -> KClass
private val typeToZenClass = HashBiMap.create<String, KClass<*>>().apply { this.populate() }

@JvmName("populate\$String\$KClass")
private fun BiMap<String, KClass<*>>.populate() {
    this[itemTagType.name] = IItemStack::class
    this[blockTagType.name] = IBlockState::class
    // TODO("fluids")
    // TODO("everything else that may require custom handling")
}

fun String.tryGetCustomClass(position: ZenPosition, environment: IEnvironmentGlobal) = typeToZenClass[this]
        ?: Object::class.also { environment.warning(position, "Tag type '$this' isn't fully supported yet: generic features won't be available") }

// native -> zen
private val nativeZenConverters = mutableMapOf<TagType<*>, (Any?) -> Any?>().apply { this.populateNz() }

@JvmName("populate\$TagType\$KFunction1\$nz")
private fun MutableMap<TagType<*>, (Any?) -> Any?>.populateNz() {
    this[itemTagType] = { (it as ItemStack).toZen(wildcard = it.metadata == OreDictionary.WILDCARD_VALUE) }
    this[blockTagType] = { (it as net.minecraft.block.state.IBlockState).toZen() }
    // TODO("fluids")
    // TODO("everything else that may require custom handling")
}

fun <T : Any, R : Any> T?.boxNative(tagType: TagType<T>): R? = (nativeZenConverters[tagType] ?: { it })(this)?.uncheckedCast()

// zen -> native
private val zenNativeConverters = mutableMapOf<TagType<*>, (Any?) -> Any?>().apply { this.populateZn() }

@JvmName("populate\$TagType\$KFunction1\$zn")
private fun MutableMap<TagType<*>, (Any?) -> Any?>.populateZn() {
    this[itemTagType] = { (it as IItemStack).toNativeStack() }
    this[blockTagType] = { (it as IBlockState).toNative() }
    // TODO("fluids")
    // TODO("everything else that may require custom handling")
}

fun <T : Any, R : Any> T?.unboxNative(tagType: TagType<T>): R? = (zenNativeConverters[tagType] ?: { it })(this)?.uncheckedCast()

// Other helpers
infix fun <T : Any> ZenTag<T>.isCompatibleWith(other: ZenTag<*>) = this.toNative().type.type == other.toNative().type.type

infix fun <T : Any> Array<*>.canFitIn(other: ZenTag<T>) = this.all { it != null && it::class.convertToNativeGenericType() == other.toNative().type.type }
