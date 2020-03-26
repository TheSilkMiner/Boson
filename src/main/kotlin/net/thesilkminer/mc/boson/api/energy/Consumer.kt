package net.thesilkminer.mc.boson.api.energy

import net.thesilkminer.mc.boson.api.direction.Direction
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
@ExperimentalUnsignedTypes
interface Consumer {
    fun tryAccept(power: ULong, from: Direction): ULong

    fun send(power: ULong, from: Direction): ULong = this.tryAccept(power, from)
}
