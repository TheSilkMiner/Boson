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

package net.thesilkminer.mc.boson.implementation.compatibility

import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.compatibility.CompatibilityProvider
import net.thesilkminer.mc.boson.api.log.L
import java.util.ServiceLoader
import kotlin.reflect.KClass

internal class ServiceBasedCompatibilityLoader<T : CompatibilityProvider> (private val provider: KClass<T>)  {
    companion object {
        private val l = L(MOD_NAME, "Compatibility Loader")
    }

    internal val providers: Sequence<T>

    init {
        l.info("Beginning discovery of implementations of compatibility provider class '${this.provider.qualifiedName}'")
        l.debug("Using ${this::class.simpleName}/$this for loading: lazily populating sequence")
        val implementations = ServiceLoader.load(this.provider.java)
        val targetSeq = implementations.asSequence()
                .map { it!! }
                .filter {
                    it.canLoad()
                            .apply {
                                if (!this) {
                                    l.info("Skipping loading of '${it}' since it does not meet conditions")
                                } else {
                                    l.debug("Loading integration '${it}' for provider")
                                }
                            }
                }
        // This is mainly done so that all classes are loaded and initialized immediately, so that mistakes or weird
        // classloading issues appear immediately during discovery, rather than somewhere else
        l.info("Discovery has found ${targetSeq.count()} loaders: returning them as sequence now")
        if (targetSeq.count() == 0) {
            l.warn("No implementations were found for '${this.provider.qualifiedName}'! This may be a problem!")
        }
        this.providers = targetSeq
    }
}
