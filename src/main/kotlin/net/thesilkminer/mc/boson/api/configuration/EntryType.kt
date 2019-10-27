package net.thesilkminer.mc.boson.api.configuration

enum class EntryType(val default: Any) {
    STRING(""),
    REAL_NUMBER(0.0),
    WHOLE_NUMBER(0),
    BOOLEAN(false),
    OBJECT(Any()),
    LIST_OF_STRINGS(listOf<String>()),
    LIST_OF_REAL_NUMBERS(listOf<Double>()),
    LIST_OF_WHOLE_NUMBERS(listOf<Int>()),
    LIST_OF_BOOLEANS(listOf<Boolean>()),
    LIST_OF_OBJECTS(Any())
}
