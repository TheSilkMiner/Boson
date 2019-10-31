@file:JvmName("DistExecutor")

package net.thesilkminer.mc.boson.api.distribution

import net.thesilkminer.mc.boson.api.bosonApi

fun <T> runSided(client: () -> () -> T, server: () -> () -> T) = when (bosonApi.currentDistribution) {
    Distribution.DEDICATED_SERVER -> server()()
    Distribution.CLIENT -> client()()
}
fun <T> onlyOn(distribution: Distribution, block: () -> () -> T?) = when (distribution) {
    Distribution.CLIENT -> runSided(client = block, server = { { null } })
    Distribution.DEDICATED_SERVER -> runSided(client = { { null } }, server = block)
}
fun clientOnly(block: () -> () -> Unit) = onlyOn(Distribution.CLIENT, block)
