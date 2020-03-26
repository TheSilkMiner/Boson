package net.thesilkminer.mc.boson.api.energy

import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
@ExperimentalUnsignedTypes
interface Holder {
    val storedPower: ULong
    val maximumCapacity: ULong
}
