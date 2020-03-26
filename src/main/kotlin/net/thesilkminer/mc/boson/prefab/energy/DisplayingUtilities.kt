@file:JvmName("DU")

package net.thesilkminer.mc.boson.prefab.energy

const val MEASUREMENT_UNIT = "A"

@ExperimentalUnsignedTypes
fun ULong.toUserFriendlyAmount(): String {
    val roundingData = this.roundToSmallestDouble()
    return "${roundingData.first} ${roundingData.second.toUnitMultiplier()}$MEASUREMENT_UNIT"
}

@ExperimentalUnsignedTypes
fun ULong.toTargetUnit() = "${this.roundToSmallestDouble().second.toUnitMultiplier()}$MEASUREMENT_UNIT"

@ExperimentalUnsignedTypes
private fun ULong.roundToSmallestDouble(): Pair<Double, Int> {
    if (this == 0UL) return 0.0 to 0

    // Explanation of what this.toDouble() does:
    // Example:
    // - Starting from 2_554_654_782_400_000UL
    // - Applying the unsigned right shift gives us 1_247_390_030_468, which converted to double is almost equal to 1.247390030468E12
    // - Multiplying it by 0b1000_0000_0000 (which is 2048), gives us 2.554654782398464E15, which represents the topmost part of the ULong value
    // - The subsequent & with 0b0111_1111_1111 gives us a value between 0 and 2047 (in this case 1536), which represents how much we need to add to obtain
    //   some more precision with the representance of the value
    // - At the end of the calculation, we end up with 2.5546547824E15, which is (surprisingly I admit) near the original value, with as much precision as possible

    var doubleEquivalent = this.toDouble()
    var rounds = 0
    while ((doubleEquivalent / 1000.0) > 0) {
        doubleEquivalent /= 1000.0
        ++rounds
    }

    return doubleEquivalent to rounds
}

private fun Int.toUnitMultiplier() = when (this) {
    0 -> ""
    1 -> "k"
    2 -> "M"
    3 -> "G"
    4 -> "T"
    5 -> "P"
    6 -> "E"
    7 -> "Z"
    8 -> "Y"
    else -> if (this < 0) throw IllegalStateException("Rounds was negative") else throw UnsupportedOperationException("$this is outside bounds for units")
}
