package ru.gnylka.smh.processing

class ProcessingPluginException : Exception {

    var pluginName: String = ""

    constructor(message: String?) : super(message)

    constructor(cause: Throwable?) : super(cause)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

}