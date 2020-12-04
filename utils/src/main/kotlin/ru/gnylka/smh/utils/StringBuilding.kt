package ru.gnylka.smh.utils

operator fun StringBuilder.plus(value: String) =  apply { append(value) }
operator fun StringBuilder.plus(value: Boolean) = apply { append(value) }
operator fun StringBuilder.plus(value: Byte) =    apply { append(value) }
operator fun StringBuilder.plus(value: Char) =    apply { append(value) }
operator fun StringBuilder.plus(value: Short) =   apply { append(value) }
operator fun StringBuilder.plus(value: Int) =     apply { append(value) }
operator fun StringBuilder.plus(value: Long) =    apply { append(value) }
operator fun StringBuilder.plus(value: Float) =   apply { append(value) }
operator fun StringBuilder.plus(value: Double) =  apply { append(value) }
operator fun StringBuilder.plus(value: Any?) =    apply { append(value) }

operator fun StringBuilder.plusAssign(value: String)  { appendLine(value) }
operator fun StringBuilder.plusAssign(value: Boolean) { appendLine(value) }
operator fun StringBuilder.plusAssign(value: Byte)    { appendLine(value) }
operator fun StringBuilder.plusAssign(value: Char)    { appendLine(value) }
operator fun StringBuilder.plusAssign(value: Short)   { appendLine(value) }
operator fun StringBuilder.plusAssign(value: Int)     { appendLine(value) }
operator fun StringBuilder.plusAssign(value: Long)    { appendLine(value) }
operator fun StringBuilder.plusAssign(value: Float)   { appendLine(value) }
operator fun StringBuilder.plusAssign(value: Double)  { appendLine(value) }
operator fun StringBuilder.plusAssign(value: Any?)    { appendLine(value) }

fun StringBuilder.appendf(format: String, vararg args: Any?) {
    this + format.format(*args)
}

// Kotlin's prependIndent does not handle final new line
// (it adds indentValue there, which causes format issues)
fun String.indentBy(indentValue: String) =
        prependIndent(indentValue).removeSuffix(indentValue)
