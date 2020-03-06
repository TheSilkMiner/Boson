@file:JvmName("DP")

package net.thesilkminer.mc.boson.api.database

import net.minecraftforge.fml.common.Loader
import net.thesilkminer.mc.boson.api.bosonApi

val databasePath get() = getDatabasePathFor(Loader.instance().activeModContainer()?.modId ?: throw IllegalStateException("Unable to determine owner automatically"))

fun getDatabasePathFor(owner: String) = bosonApi.getDatabasePathFor(owner)
