@file:JvmName("FRI")

package net.thesilkminer.mc.boson.implementation.registry

import com.google.common.collect.BiMap
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.ForgeRegistry
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryManager
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import java.lang.reflect.Field

private val registriesField: Field by lazy { RegistryManager::class.java.getDeclaredField("registries").apply { this.isAccessible = true } }

internal val IForgeRegistry<*>.name: ResourceLocation? get() = (this as? ForgeRegistry<*>)?.name
internal val ForgeRegistry<*>.name: ResourceLocation? get() = RegistryManager.ACTIVE.registries.inverse()[this]

internal val RegistryManager.registries: BiMap<ResourceLocation, ForgeRegistry<*>> get() = registriesField.get(this).uncheckedCast()
