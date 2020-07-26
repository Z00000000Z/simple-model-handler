package ru.gnylka.smh.model.converter.cli

const val ILLEGAL_VERBOSITY_FLAGS_COUNT = "Expected no more than %s verbosity flags (got %s)"
const val NO_SUCH_FILE = "No such file: %s"
const val IO_EXCEPTION_OCCURED = "An I/O exception occured while reading files: %s"
const val UNKNOWN_EXCEPTION_OCCURED = "Unknown exception occured while reading files: %s"

const val LOG_FILE_NOT_FOUND = "File not found: %s"
const val LOG_PROCESSING_EXCEPTION = "An exception occured during processing: %s"
const val LOG_ILLEGAL_PROPERTY =
        "Found invalid property (use --skip-invalid-properties to suppress): %s"
const val LOG_PROCESSING_PLUGIN_EXCEPTION =
        "An exception occured during processing in plugin %s: %s"
const val LOG_FILE_ALREADY_EXISTS = "File already exists: %s"
const val LOG_UNKNOWN_IO_EXCEPTION = "An I/O exception occured: %s"
const val LOG_UNKNOWN_EXCEPTION = "Unexpected exception occured: %s"

const val LOG_LOADED_MODEL = "Loaded model from %s"
const val LOG_CONVERTED_MODEL = "Converted model %s"
const val LOG_OPTIMIZED_MODEL = "Optimized model %s"
const val LOG_WROTE_MODEL = "Wrote model to %s"
