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

package net.thesilkminer.mc.boson.implementation

import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.minecraftforge.registries.RegistryBuilder
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.ExperimentalBosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.api.registry.DeferredRegister
import net.thesilkminer.mc.boson.api.registry.RegistryObject
import net.thesilkminer.mc.boson.implementation.registry.BosonDeferredRegister
import net.thesilkminer.mc.boson.implementation.registry.BosonRegistryObject
import kotlin.reflect.KClass

class ExperimentalBosonApiBinding : ExperimentalBosonApi {
    private val l = L(MOD_NAME, "Experimental Bindings")

    init {
        l.warn("A mod has requested Experimental APIs to be loaded: if you experience crashes it may due to mismatching versions")
    }

    override fun <T : IForgeRegistryEntry<T>> createDeferredRegister(owner: String, registry: IForgeRegistry<T>): DeferredRegister<T> = BosonDeferredRegister(owner, registry)
    override fun <T : IForgeRegistryEntry<T>> createDeferredRegister(owner: String, type: KClass<T>, name: String, registryFactory: RegistryBuilder<T>.() -> Unit): DeferredRegister<T>
            = BosonDeferredRegister(owner, type, name, registryFactory)
    override fun <T : IForgeRegistryEntry<T>> createDeferredRegister(owner: String, type: KClass<T>): DeferredRegister<T> = BosonDeferredRegister(owner, type)
    override fun <T : IForgeRegistryEntry<T>, U : T> createRegistryObject(name: NameSpacedString, registryType: () -> KClass<out T>): RegistryObject<U> = BosonRegistryObject(name, registryType)
    override fun <T : IForgeRegistryEntry<T>, U : T> createRegistryObject(name: NameSpacedString, registry: IForgeRegistry<T>): RegistryObject<U> = BosonRegistryObject(name, registry)
    override fun <T : IForgeRegistryEntry<T>, U : T> createRegistryObject(name: NameSpacedString, baseType: KClass<T>, modId: String): RegistryObject<U> = BosonRegistryObject(name, baseType, modId)
    override fun <T : IForgeRegistryEntry<in T>> obtainEmptyRegistryObject(): RegistryObject<T> = BosonRegistryObject()
}
