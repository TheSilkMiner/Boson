package net.thesilkminer.mc.boson.prefab.loader.processor

import net.thesilkminer.kotlin.commons.lang.extractMessage
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.Processor
import net.thesilkminer.mc.boson.api.log.L

class CatchingProcessor<T : Any>(private val logger: L = L("Boson Loader", "CatchingProcessor"), private val processor: Processor<T>) : Processor<T> {
    private class GoThroughException(val wrapped: Exception) : Exception()

    companion object {
        fun throwException(e: Exception): Nothing = throw GoThroughException(e)
    }

    override fun process(content: T, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?) {
        try {
            this.processor.process(content, identifier, globalContext, phaseContext)
        } catch (e: Exception) {
            if (e is GoThroughException) throw e.wrapped
            with (this.logger) {
                this.bigError("""
                    An error has occurred while attempting to process the file '$identifier'.
                    Error message: ${e.extractMessage()}
                    Exception type: ${e::class.simpleName}
                    Name of the file that caused the error: $identifier
                    
                    The full stacktrace will be printed to STDERR in the text that follows.
                """.trimIndent(), dumpStack = L.DumpStackBehavior.DO_NOT_DUMP)
                e.printStackTrace(System.err)
            }
        }
    }
}
