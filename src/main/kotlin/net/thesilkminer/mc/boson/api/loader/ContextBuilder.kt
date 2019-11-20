package net.thesilkminer.mc.boson.api.loader

interface ContextBuilder {
    fun buildContext(phase: LoadingPhase<*>?, vararg any: Any): Context
}
