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

package net.thesilkminer.mc.boson.hook

import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ProgressManager
import net.minecraftforge.fml.common.event.FMLEvent
import net.thesilkminer.mc.boson.api.event.BosonPreAvailableEvent

@Suppress("unused")
object GameDataHook {
    private const val PRE_INIT_MARKER = "\$Boson\$marker\$UsePreInit"

    private fun reflectProgressBarFromLoader() = with(Loader::class.java.getDeclaredField("progressBar")) {
        this.isAccessible = true
        this.get(Loader.instance()) as ProgressManager.ProgressBar
    }

    @JvmStatic
    fun showPreInitializationCreationBar() {
        this.reflectProgressBarFromLoader().step(PRE_INIT_MARKER)
    }

    @JvmStatic
    fun stepPopulatingRegistryBar() {
        this.reflectProgressBarFromLoader().step("Populating Registries")
    }

    @JvmStatic
    fun stepBosonAvailableBar() {
        this.reflectProgressBarFromLoader().step("Propagating Loading Completion")
    }

    @JvmStatic
    fun getDescription(event: FMLEvent) = if (event is BosonPreAvailableEvent) "BosonPreAvailable" else event.description()!!

    @JvmStatic
    fun getSortingComparator() = Comparator<Any> { o1, o2 -> o1.toString().compareTo(o2.toString(), ignoreCase = true) }
}
