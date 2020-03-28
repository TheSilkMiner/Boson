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

package net.thesilkminer.mc.boson.implementation

import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.ExperimentalBosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.api.registry.DeferredRegister
import net.thesilkminer.mc.boson.api.registry.RegistryObject
import net.thesilkminer.mc.boson.implementation.registry.BosonDeferredRegister
import net.thesilkminer.mc.boson.implementation.registry.BosonRegistryObject

class ExperimentalBosonApiBinding : ExperimentalBosonApi {
    private val l = L(MOD_NAME, "Experimental Bindings")

    init {
        l.warn("A mod has requested Experimental APIs to be loaded: if you experience crashes it may due to mismatching versions")
    }

    override fun <T : IForgeRegistryEntry<T>> createDeferredRegister(registry: IForgeRegistry<T>, owner: String): DeferredRegister<T> = BosonDeferredRegister(registry, owner)
    override fun <T : IForgeRegistryEntry<T>, U : T> createRegistryObject(name: NameSpacedString, registry: IForgeRegistry<T>): RegistryObject<U> = BosonRegistryObject(name, registry)
}
