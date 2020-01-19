package net.thesilkminer.mc.boson.prefab.loader.preprocessor

import net.thesilkminer.kotlin.commons.lang.extractMessage
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.Preprocessor
import net.thesilkminer.mc.boson.api.log.L
import java.io.PrintWriter
import java.io.StringWriter

class CatchingPreprocessor<T : Any, R : Any>(private val logger: L = L("Boson Loader", "CatchingPreprocessor"), private val preprocessor: Preprocessor<T, R>) : Preprocessor<T, R> {
    private class GoThroughException(val wrapped: Exception) : Exception()
    private class CustomMessageException(val msg: String, val wrapped: Exception) : Exception()

    companion object {
        fun throwException(e: Exception): Nothing = throw GoThroughException(e)
        fun withCustomMessage(e: Exception, message: String): Nothing = throw CustomMessageException(message, e)
    }

    override fun preProcessData(content: T, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?): R? {
        return try {
            this.preprocessor.preProcessData(content, identifier, globalContext, phaseContext)
        } catch (e: Exception) {
            if (e is GoThroughException) throw e.wrapped
            with (this.logger) {
                val errorMessage = (if (e is CustomMessageException) e.wrapped else e).extractMessage()
                val exceptionType = (if (e is CustomMessageException) e.wrapped else e)::class.simpleName
                val customMessage = if (e is CustomMessageException) "\n\n${e.msg}\n\n" else ""
                val stringWriter = StringWriter()
                e.printStackTrace(PrintWriter(stringWriter))
                val msgFixedPart = """
                    An error has occurred while attempting to pre-process the file '$identifier'.
                    Error message: $errorMessage$customMessage
                    Exception type: $exceptionType
                    Name of the file that caused the error: $identifier
                """.trimIndent()
                this.bigError("$msgFixedPart\n\nThe full stacktrace is in the text that follows:\n$stringWriter", dumpStack = L.DumpStackBehavior.DO_NOT_DUMP)
            }
            null
        }
    }
}