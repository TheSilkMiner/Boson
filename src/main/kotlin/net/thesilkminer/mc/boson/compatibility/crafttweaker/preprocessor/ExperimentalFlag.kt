package net.thesilkminer.mc.boson.compatibility.crafttweaker.preprocessor

enum class ExperimentalFlag(val flagName: String, val isDeprecated: Boolean = false) {
    SEQUENCE_OPERATOR_OVERLOADING("-Esoo"),
    SEQUENCE_AUTOMATIC_IT_ARGUMENT("-Esaia")
}
