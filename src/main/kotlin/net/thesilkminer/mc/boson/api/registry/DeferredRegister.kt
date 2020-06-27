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

package net.thesilkminer.mc.boson.api.registry

import net.minecraftforge.fml.common.eventhandler.EventBus
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.minecraftforge.registries.RegistryBuilder
import net.thesilkminer.mc.boson.api.experimentalBosonApi
import org.jetbrains.annotations.ApiStatus
import kotlin.reflect.KClass

@ApiStatus.Experimental
interface DeferredRegister<T : IForgeRegistryEntry<T>> {
    companion object {
        fun <T : IForgeRegistryEntry<T>> obtain(owner: String, registry: IForgeRegistry<T>): DeferredRegister<T> = experimentalBosonApi.createDeferredRegister(owner, registry)
        fun <T : IForgeRegistryEntry<T>> obtain(owner: String, type: KClass<T>, name: String, registryFactory: () -> RegistryBuilder<T>): DeferredRegister<T> =
                experimentalBosonApi.createDeferredRegister(owner, type, name, registryFactory)
        fun <T : IForgeRegistryEntry<T>> obtain(owner: String, type: KClass<T>): DeferredRegister<T> = experimentalBosonApi.createDeferredRegister(owner, type)

        operator fun <T : IForgeRegistryEntry<T>> invoke(owner: String, registry: IForgeRegistry<T>) = this.obtain(owner, registry)
        operator fun <T : IForgeRegistryEntry<T>> invoke(owner: String, type: KClass<T>, name: String, registryFactory: () -> RegistryBuilder<T>) =
                this.obtain(owner, type, name, registryFactory)
        operator fun <T : IForgeRegistryEntry<T>> invoke(owner: String, type: KClass<T>) = this.obtain(owner, type)
    }

    val registryType: KClass<T>
    val registry: IForgeRegistry<T>
    val owner: String

    fun <U : T> register(name: String, objectSupplier: () -> U): RegistryObject<U>

    fun subscribeOnto(bus: EventBus) // TODO("API EventBus out")
}
