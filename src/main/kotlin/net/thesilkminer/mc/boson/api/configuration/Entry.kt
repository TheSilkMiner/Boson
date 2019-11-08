package net.thesilkminer.mc.boson.api.configuration

interface Entry {
    class View(private val value: Any) {
        val boolean: Boolean get() = this.value as Boolean
        val string: String get() = this.value as String
        val int: Int get() = this.long.toInt()
        val short: Short get() = this.long.toShort()
        val byte: Byte get() = this.long.toByte()
        val long: Long get() = this.value as Long
        val double: Double get() = this.value as Double
        val float: Float get() = this.double.toFloat()
        val any: Any get() = this.value

        @Suppress("UNCHECKED_CAST")
        fun <T> asList() : List<T> = this.value as List<T>
    }

    val name: String
    val type: EntryType
    val comment: String
    val languageKey: String
    val default: Any
    val requiresMcRestart: Boolean
    val requiresWorldReload: Boolean
    val hasSlider: Boolean
    val bounds: Pair<Any?, Any?>

    var currentValue: Any

    operator fun invoke() = View(this.currentValue)
}
