@file:JvmName("CCK")

package net.thesilkminer.mc.boson.prefab.loader

import net.thesilkminer.mc.boson.api.bosonApi

val modIdContextKey by lazy { bosonApi.createLoaderContextKey("modId", String::class) }
