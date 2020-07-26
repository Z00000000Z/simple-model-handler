package ru.gnylka.smh.model.converter.cli

import java.nio.file.Path
import java.util.logging.Logger

data class SessionData(
        val fromFile: Path,
        val toFile: Path,
        val isBinary: Boolean,
        val propertyPrefix: String,
        val skipInvalidProperties: Boolean,
        val usePlugins: Set<String>,
        val pluginArguments: Map<String, Array<String>>,
        val pickFirstTexture: Boolean,
        val skipNormals: Boolean,
        val optimize: Set<OptimizationOptions>,
        val compress: Boolean,
        val logger: Logger,
        val modelName: String
)
