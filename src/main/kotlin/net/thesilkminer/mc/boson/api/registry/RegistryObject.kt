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

package net.thesilkminer.mc.boson.api.registry

import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.api.experimentalBosonApi
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import org.jetbrains.annotations.ApiStatus
import kotlin.reflect.KClass

@ApiStatus.Experimental
interface RegistryObject<T : IForgeRegistryEntry<in T>> {
    companion object {
        // TODO("IForgeRegistry -> Registry")
        fun <T : IForgeRegistryEntry<T>, U : T> create(name: NameSpacedString, registryType: () -> KClass<out T>): RegistryObject<U>
                = experimentalBosonApi.createRegistryObject(name, registryType)
        fun <T : IForgeRegistryEntry<T>, U : T> create(name: NameSpacedString, registry: IForgeRegistry<T>): RegistryObject<U> = experimentalBosonApi.createRegistryObject(name, registry)
        fun <T : IForgeRegistryEntry<T>, U : T> create(name: NameSpacedString, baseType: KClass<T>, modId: String): RegistryObject<U> =
                experimentalBosonApi.createRegistryObject(name, baseType, modId)

        fun <T : IForgeRegistryEntry<in T>> empty(): RegistryObject<T> = experimentalBosonApi.obtainEmptyRegistryObject()

        operator fun <T : IForgeRegistryEntry<T>, U : T> invoke(name: NameSpacedString, registryType: () -> KClass<out T>): RegistryObject<U> = this.create(name, registryType)
        operator fun <T : IForgeRegistryEntry<T>, U : T> invoke(name: NameSpacedString, registry: IForgeRegistry<T>): RegistryObject<U> = create(name, registry)
        operator fun <T : IForgeRegistryEntry<T>, U : T> invoke(name: NameSpacedString, baseType: KClass<T>, modId: String): RegistryObject<U> = this.create(name, baseType, modId)
    }

    val name: NameSpacedString
    val value: T?

    fun get() = this.value!!

    operator fun invoke() = this.get()
}
