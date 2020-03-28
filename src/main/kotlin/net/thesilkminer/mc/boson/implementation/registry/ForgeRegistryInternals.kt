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
