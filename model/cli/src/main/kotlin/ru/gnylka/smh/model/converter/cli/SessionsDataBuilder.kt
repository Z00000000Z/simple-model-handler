package ru.gnylka.smh.model.converter.cli

import ru.gnylka.smh.loader.getAvailableLoaders
import ru.gnylka.smh.model.converter.cli.arguments.ArgumentsContainer
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.util.logging.Level
import java.util.logging.Logger

private val LOGGING_LEVELS = arrayOf(
        Level.WARNING,
        Level.INFO,
        Level.FINE
)

val MAX_LOGGING_LEVEL = LOGGING_LEVELS.size - 1

val knownExtensions = getAvailableLoaders().map { it.fileExtensions }.flatten().toSet()

fun getSessionsDataFromArguments(container: ArgumentsContainer) =
        getInputFiles(container.input ?: emptyArray())
                .map { getDataForFile(it, container.output, container.fileType) }
                .map { getSessionData(it, container) }

private fun getInputFiles(inputPaths: Array<Path>) =
        inputPaths.map {
            verifyExists(it)
            if (Files.isDirectory(it)) getFilesInDirectory(it)
            else listOf(it)
        }.flatten()

private fun verifyExists(path: Path) {
    if (Files.notExists(path))
        throw NoSuchFileException(path.toAbsolutePath().toString())
}

private fun getFilesInDirectory(directoryPath: Path) =
        Files.newDirectoryStream(directoryPath) {
            val fileName = it.fileName.toString()
            val extension = getExtension(fileName)

            val isKnownExtension = extension in knownExtensions
            val isRegular = Files.isRegularFile(it)

            isKnownExtension && isRegular
        }.toList()

private fun getDataForFile(inputFile: Path,
                           outputDir: Path?,
                           fileType: OutputFileType?): FilesData {
    val type = fileType ?: OutputFileType.BINARY
    val noExtension = getFileNameWithoutExtension(inputFile.fileName.toString())
    val outputFileName = "$noExtension.${type.fileExtension}"

    val output = if (outputDir == null) {
        val input = inputFile.parent ?: Path.of(".")
        input.resolve(outputFileName)
    } else outputDir.resolve(outputFileName)

    return FilesData(inputFile, output, type)
}

private fun getSessionData(filesData: FilesData,
                           container: ArgumentsContainer): SessionData {
    val args = container.pluginArguments ?: emptyMap()
    val splittedArguments = args.map {
        it.key to it.value.split(" ").toTypedArray()
    }.toMap()


    val optimizationOptions = container.optimize?.let {
        if (it.isEmpty()) OptimizationOptions.values().toSet()
        else it.toSet()
    } ?: emptySet()

    val modelName = getFileNameWithoutExtension(filesData.fromFile.fileName.toString())
    val logger = Logger.getLogger(modelName)
    logger.level = getVerboseLevel(container.verbose)

    return SessionData(
            filesData.fromFile,
            filesData.toFile,
            filesData.fileType === OutputFileType.BINARY,
            container.propertyPrefix!!,
            container.skipInvalidProperties,
            (container.usePlugins ?: emptyArray()).toSet(),
            splittedArguments,
            container.pickFirstTexture,
            container.skipNormals,
            optimizationOptions,
            container.compress,
            logger,
            modelName
    )
}

fun getVerboseLevel(verbose: BooleanArray?): Level {
    val size = verbose?.size ?: 0
    require(size < LOGGING_LEVELS.size) {
        ILLEGAL_VERBOSITY_FLAGS_COUNT.format(MAX_LOGGING_LEVEL, size)
    }
    return LOGGING_LEVELS[size]
}
