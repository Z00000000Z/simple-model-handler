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

operator fun StringBuilder.plusAssign(value: String)  { appendln(value) }
operator fun StringBuilder.plusAssign(value: Boolean) { appendln(value) }
operator fun StringBuilder.plusAssign(value: Byte)    { appendln(value) }
operator fun StringBuilder.plusAssign(value: Char)    { appendln(value) }
operator fun StringBuilder.plusAssign(value: Short)   { appendln(value) }
operator fun StringBuilder.plusAssign(value: Int)     { appendln(value) }
operator fun StringBuilder.plusAssign(value: Long)    { appendln(value) }
operator fun StringBuilder.plusAssign(value: Float)   { appendln(value) }
operator fun StringBuilder.plusAssign(value: Double)  { appendln(value) }
operator fun StringBuilder.plusAssign(value: Any?)    { appendln(value) }

fun StringBuilder.appendf(format: String, vararg args: Any?) {
    this + format.format(*args)
}

// Kotlin's prependIndent does not handle final new line
// (it adds indentValue there, which causes ru.gnylka.smh.file.g3dj.format issues)
fun String.indentBy(indentValue: String) =
        prependIndent(indentValue).removeSuffix(indentValue)
