/*
 * Copyright (C) 2020  TheSilkMiner
 *
 * This file is part of Boson.
 *
 * Boson is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Boson is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Boson.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact information:
 * E-mail: thesilkminer <at> outlook <dot> com
 */

package net.thesilkminer.mc.boson.prefab.loader.processor

import net.thesilkminer.kotlin.commons.lang.extractMessage
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.Processor
import net.thesilkminer.mc.boson.api.log.L
import java.io.PrintWriter
import java.io.StringWriter

class CatchingProcessor<T : Any>(private val logger: L = L("Boson Loader", "CatchingProcessor"), private val processor: Processor<T>) : Processor<T> {
    private class GoThroughException(val wrapped: Exception) : Exception()
    private class CustomMessageException(val msg: String, val wrapped: Exception) : Exception()

    companion object {
        fun throwException(e: Exception): Nothing = throw GoThroughException(e)
        fun withCustomMessage(e: Exception, message: String): Nothing = throw CustomMessageException(message, e)
    }

    override fun process(content: T, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?) {
        try {
            this.processor.process(content, identifier, globalContext, phaseContext)
        } catch (e: Exception) {
            if (e is GoThroughException) throw e.wrapped
            with (this.logger) {
                val errorMessage = (if (e is CustomMessageException) e.wrapped else e).extractMessage()
                val exceptionType = (if (e is CustomMessageException) e.wrapped else e)::class.simpleName
                val customMessage = if (e is CustomMessageException) "\n\n${e.msg}\n\n" else ""
                val stringWriter = StringWriter()
                e.printStackTrace(PrintWriter(stringWriter))
                val msgFixedPart = """
                    An error has occurred while attempting to process the file '$identifier'.
                    Error message: $errorMessage$customMessage
                    Exception type: $exceptionType
                    Name of the file that caused the error: $identifier
                """.trimIndent()
                this.bigError("$msgFixedPart\n\nThe full stacktrace is in the text that follows:\n$stringWriter", dumpStack = L.DumpStackBehavior.DO_NOT_DUMP)
            }
        }
    }
}
