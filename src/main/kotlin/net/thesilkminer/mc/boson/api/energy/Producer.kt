package net.thesilkminer.mc.boson.api.energy

import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
@ExperimentalUnsignedTypes
interface Producer {
    val producedPower: ULong
}
