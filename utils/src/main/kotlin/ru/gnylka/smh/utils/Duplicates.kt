package ru.gnylka.smh.utils

inline fun <T> Iterable<T>.findDuplicates(
        messageSupplier: (T, Int) -> String
) = findDuplicates({ it }, messageSupplier)

inline fun <T, R> Iterable<T>.findDuplicates(
        compareBy: (T) -> R,
        messageSupplier: (R, Int) -> String
) = findDuplicatesAndThrow(compareBy) { element, count ->
    throw RuntimeException(messageSupplier(element, count))
}

inline fun <T> Iterable<T>.findDuplicatesAndThrow(
        exceptionProducer: (T, Int) -> Nothing
) = findDuplicatesAndThrow({ it }, exceptionProducer)

inline fun <T, R> Iterable<T>.findDuplicatesAndThrow(
        compareBy: (T) -> R,
        exceptionProducer: (R, Int) -> Nothing
) = groupBy(compareBy).forEach { (key, value) ->
    if (value.size != 1) exceptionProducer(key, value.size)
}
