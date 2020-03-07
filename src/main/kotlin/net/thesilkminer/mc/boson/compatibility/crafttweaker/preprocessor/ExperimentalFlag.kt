package net.thesilkminer.mc.boson.compatibility.crafttweaker.preprocessor

internal enum class ExperimentalFlag(internal val flagName: String, internal val isDeprecated: Boolean = false) {
    SEQUENCE_OPERATOR_OVERLOADING("-Esoo"),
    SEQUENCE_AUTOMATIC_IT_ARGUMENT("-Esaia")
}
