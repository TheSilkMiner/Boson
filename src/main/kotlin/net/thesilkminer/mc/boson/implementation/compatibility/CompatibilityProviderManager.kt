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

package net.thesilkminer.mc.boson.implementation.compatibility

import net.minecraftforge.common.MinecraftForge
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityProvider
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityProviderRegistry
import net.thesilkminer.mc.boson.api.event.CompatibilityProviderRegistryEvent
import net.thesilkminer.mc.boson.api.log.L
import kotlin.reflect.KClass

internal object CompatibilityProviderManager : CompatibilityProviderRegistry {
    private val l = L(MOD_NAME, "Compatibility Provider Registry")

    private val providers = mutableSetOf<KClass<out CompatibilityProvider>>()
    private val loaders = mutableMapOf<KClass<out CompatibilityProvider>, ServiceBasedCompatibilityLoader<out CompatibilityProvider>>()

    override fun <T : CompatibilityProvider> registerProvider(provider: KClass<out T>) {
        if (provider in this.providers) {
            l.bigWarn("A handler for the given class ${provider.qualifiedName} was registered before: registering another is NOT supported! Addition will be skipped")
            return
        }
        this.providers += provider
    }

    override fun findAllProviders(): Sequence<CompatibilityProvider> = this.providers.asSequence().map(this::findProviders).flatten()

    override fun <T : CompatibilityProvider> findProviders(provider: KClass<out T>): Sequence<T> =
        this.loaders[provider]?.providers?.uncheckedCast() ?: this.l.warn("Provider '${provider.qualifiedName}' wasn't registered!").let { sequenceOf<T>() }

    internal fun registerProviders() {
        this.l.info("Beginning provider registration")
        MinecraftForge.EVENT_BUS.post(CompatibilityProviderRegistryEvent(this))
        this.l.info("Registration completed: a total of ${this.providers.count()} were registered")
        this.l.info("Creating and discovering implementations for each of them")
        this.providers.forEach { this.loaders[it] = ServiceBasedCompatibilityLoader(it) }
        this.l.info("Providers registered")
    }

    internal fun fire(event: CompatibilityProvider.() -> Unit) {
        this.providers.asSequence()
                .map(this::findProviders)
                .flatten()
                .forEach(event)
    }
}
