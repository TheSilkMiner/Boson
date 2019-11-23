@file:JvmName("U")

package net.thesilkminer.mc.boson.api.log

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.math.min

class L(id: String, marker: String = "") : Logger by LogManager.getLogger("$id${if (marker.isBlank()) "" else "/$marker"}") {
    enum class DumpStackBehavior {
        FULL_DUMP,
        NORMAL_DUMP,
        DO_NOT_DUMP
    }

    companion object {
        @Suppress("SpellCheckingInspection")
        fun r(marker: String = "") = L("RE/SYST", marker)
    }

    fun logAndDump(message: String, dumpStack: DumpStackBehavior = DumpStackBehavior.FULL_DUMP) {
        this.doBigMessage(message, dumpStack) {
            this.info(it)
        }
    }

    fun bigWarn(message: String, dumpStack: DumpStackBehavior = DumpStackBehavior.NORMAL_DUMP) {
        this.doBigMessage(message, dumpStack) {
            this.warn(it)
        }
    }

    fun bigError(message: String, dumpStack: DumpStackBehavior = DumpStackBehavior.NORMAL_DUMP) {
        this.doBigMessage(message, dumpStack) {
            this.error(it)
        }
    }

    private fun doBigMessage(message: String, dsb: DumpStackBehavior, logFun: (String) -> Any?) {
        val messageBuilder = StringBuilder()

        val lines = message.addDumpIfNeeded(dsb).injectStartAndStopNewLine().replaceAllTabs().lines()
        val maxLineLength = (lines.asSequence().map { it.length }.max() ?: 0)
        val maxLength = maxLineLength + 4

        for (i in 0 until maxLength) messageBuilder += '*'
        messageBuilder += '\n'

        lines.forEach {
            messageBuilder += "* "
            messageBuilder += it
            for (i in 0 until -(it.length - maxLineLength)) messageBuilder += ' '
            messageBuilder += " *\n"
        }

        for (i in 0 until maxLength) messageBuilder += '*'

        val logLines = messageBuilder().split('\n')
        -messageBuilder
        logLines.forEach { logFun(it) }
    }

    private fun String.injectStartAndStopNewLine() = " \n${this}\n "
    private fun String.replaceAllTabs() = this.replace("\t", "    ")
    private fun String.addDumpIfNeeded(dsb: DumpStackBehavior) = if (dsb == DumpStackBehavior.DO_NOT_DUMP) this else doDump(this, dsb)
}

private fun doDump(initialString: String, dsb: L.DumpStackBehavior): String {
    val stack = Throwable().stackTrace
    val builder = StringBuilder()
    builder += initialString
    builder += "\n\n"
    val start = if (dsb == L.DumpStackBehavior.FULL_DUMP) 0 else 5
    val endT = if (dsb == L.DumpStackBehavior.NORMAL_DUMP) min(stack.count(), 4) else stack.count()
    val end = min(start + endT, stack.count())
    for (i in start until end) {
        builder += stack[i].toPrintableString()
        if (i != end - 1) builder += '\n'
    }
    if (dsb == L.DumpStackBehavior.NORMAL_DUMP) builder += "\n... (Rest of stack dump omitted)"
    return builder()
}

private fun StackTraceElement.toPrintableString() =
        if (this.isNativeMethod) "at ${this.className}.${this.methodName} (in JNI)"
        else "at ${this.className}.${this.methodName} (${if (this.fileName == null) "???" else this.fileName!!}:${if (this.lineNumber < 0) "???" else this.lineNumber.toString()})"

private operator fun StringBuilder.plusAssign(a: String) {
    this.append(a)
}
private operator fun StringBuilder.plusAssign(a: Char) {
    this.append(a)
}
private operator fun StringBuilder.invoke() = this.toString()
private operator fun StringBuilder.unaryMinus() {
    this.clear()
}
