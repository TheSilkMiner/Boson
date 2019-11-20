package net.thesilkminer.mc.boson.api.loader

interface LoadingPhase<T : Any> {
    val name: String
    val filters: List<Filter>
    val contextBuilder: ContextBuilder?
    val preprocessor: Preprocessor<String, T>?
    val processor: Processor<T>
}
