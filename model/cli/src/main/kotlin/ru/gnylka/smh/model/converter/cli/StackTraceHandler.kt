package ru.gnylka.smh.model.converter.cli

import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path

class StackTraceHandler(
        private val exceptions: List<Exception>
) {

    private val POSITIVE_ANSWERS = setOf("y", "yes")
    private val DUMP_ANSWER = setOf("dump")

    fun ask() {
        loop@for (exception in exceptions) {
            print("Print stack trace? (yes/no/dump): ")
            when (readLine()) {
                null, in POSITIVE_ANSWERS -> {
                    exception.printStackTrace()
                    println()
                }
                in DUMP_ANSWER -> {
                    dumpExceptions()
                    break@loop
                }
                else -> continue@loop
            }
        }
    }

    private fun dumpExceptions() {
        PrintWriter(Files.newBufferedWriter(Path.of("smh-model-dump"))).use { writer ->
            exceptions.forEach {
                it.printStackTrace(writer)
                writer.println()
            }
        }
    }

}
