package ru.gnylka.smh.model.converter.cli

import picocli.CommandLine
import ru.gnylka.smh.loader.getAvailableLoaders
import ru.gnylka.smh.model.converter.cli.arguments.ArgumentsContainer
import ru.gnylka.smh.processing.getAvailablePlugins
import java.io.IOException
import java.nio.file.NoSuchFileException
import java.util.*
import java.util.logging.LogManager.getLogManager
import java.util.logging.Logger
import kotlin.math.min

private const val MAX_FAILED_EXECUTORS_TO_SHOW = 5
private var isDebugMode = false
private lateinit var mainLogger: Logger

fun main(args: Array<String>) {
    Locale.setDefault(Locale.US)

    val container = ArgumentsContainer()
    val cli = CommandLine(container)
            .setCaseInsensitiveEnumValuesAllowed(true)

    val exitValue = cli.execute(*args)

    isDebugMode = container.verbose?.size == MAX_LOGGING_LEVEL
    mainLogger = createMainLogger()

    val isHelp = cli.isUsageHelpRequested || cli.isVersionHelpRequested
    if (exitValue != 0 || isHelp || container.listLoaders || container.listPlugins) {
        if (container.listLoaders) {
            if (isHelp) println()
            printAvailableLoaders()
        }

        if (container.listPlugins) {
            if (isHelp || container.listLoaders) println()
            printAvailablePlugins()
        }

        return
    }

    val sessionsData = getSessionsData(container) ?: return
    val totalSize = sessionsData.size

    mainLogger.info("Found $totalSize models to convert")

    val sessionsExecutor: SessionsExecutor =
            if (container.parallel) ConcurrentSessionsExecutor(sessionsData)
            else SequentalSessionsExecutor(sessionsData)

    val failed = sessionsExecutor.execute()
            // remove pairs where exception is null
            .mapNotNull { (data, exception) ->
                exception?.let { data to it }
            }.toMap()


    val successfulCount = totalSize - failed.size
    mainLogger.info("Successfully converted $successfulCount models of $totalSize")

    if (failed.isNotEmpty())
        handleFailed(failed)
}

private fun createMainLogger(): Logger {
    val configStream = ArgumentsContainer::class.java.getResourceAsStream("/logging.properties")
    getLogManager().readConfiguration(configStream)
    return Logger.getLogger("Main")
}

private fun getSessionsData(container: ArgumentsContainer): List<SessionData>? {
    var sessionsData: List<SessionData>? = null
    try {
        sessionsData = getSessionsDataFromArguments(container)
        mainLogger.level = sessionsData[0].logger.level
    } catch (e: NoSuchFileException) {
        mainLogger.severe(NO_SUCH_FILE.format(e.message))
    } catch (e: IOException) {
        mainLogger.severe(IO_EXCEPTION_OCCURED.format(e))
        StackTraceHandler(listOf(e)).ask()
    } catch (e: Exception) {
        mainLogger.severe(UNKNOWN_EXCEPTION_OCCURED.format(e))
        StackTraceHandler(listOf(e)).ask()
    }
    return sessionsData
}

private fun handleFailed(failed: Map<SessionData, Exception>) {
    val size = failed.size
    val failedSessionsString = "Failed to convert $size models: "
    val sessionsToShow = min(size, MAX_FAILED_EXECUTORS_TO_SHOW)

    val joiner = StringJoiner(", ")
    failed.keys.take(sessionsToShow)
            .map { it.modelName }
            .forEach { joiner.add(it) }
    if (sessionsToShow < size) joiner.add("...")
    val failedString = joiner.toString()

    mainLogger.warning(failedSessionsString + failedString)

    if (isDebugMode) StackTraceHandler(failed.values.toList()).ask()
}

private fun printAvailableLoaders() {
    val loaders = getAvailableLoaders()
    if (loaders.isEmpty()) return

    loaders.forEachIndexed { i, loader ->
        val loaderName = loader::class.java.simpleName
        val extensionsStr = loader.fileExtensions.joinToString(", ")
        println("[${i + 1}] $loaderName - $extensionsStr")
    }

    val supportedExtensionsStr = loaders.map { it.fileExtensions }.flatten().joinToString(", ")

    println()
    println("All supported extensions: $supportedExtensionsStr")
}

private fun printAvailablePlugins() {
    val plugins = getAvailablePlugins().toList()

    plugins.forEachIndexed { i, plugin ->
        println("[${i + 1}] ${plugin.pluginName}:")

        val pluginHelp = plugin.showHelp().orEmpty()
        val isLast = i == plugins.lastIndex

        print(pluginHelp + if (isLast) "" else "\n")
    }
}
