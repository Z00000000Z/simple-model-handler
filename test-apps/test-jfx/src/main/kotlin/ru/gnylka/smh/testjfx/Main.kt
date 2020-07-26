package ru.gnylka.smh.testjfx

import javafx.application.Application
import javafx.scene.shape.CullFace
import picocli.CommandLine
import ru.gnylka.smh.testjfx.arguments.ArgumentsContainer
import java.nio.file.Path

const val FAILED_TO_READ_ARGUMENTS = "Unknown exception occured while trying to read arguments"
const val NO_SUCH_FILE = "File %s does not exists"
const val FAILED_TO_OPEN_FILE = "An IO exception occured while opening file %s"
const val UNKNOWN_EXCEPTION_ON_OPENING_FILE = "Unknown exception occured while opening file %s"
const val FAILED_TO_LOAD_MODEL = "An IO exception occured while loading model"
const val UNKNOWN_EXCEPTION_ON_MODEL_LOADING = "Unknown exception occured while loading model"
const val FAILED_TO_PARSE_COLOR = "Failed to parse color %s"

lateinit var input: Path
var compress: Boolean = false
lateinit var texturesDirectory: Path
var info: Boolean = false
lateinit var background: String
var faceCulling: CullFace? = null
var light: String? = null
var scale: Double = 1.0
var planes: Double = 0.0
var acceleration: Double = 2.0
var sensitivity: Double = 0.25

fun main(args: Array<String>) {
    val container = ArgumentsContainer()
    val cli = CommandLine(container)
            .setCaseInsensitiveEnumValuesAllowed(true)

    val exitValue = cli.execute(*args)
    if (exitValue != 0 ||
            cli.isUsageHelpRequested ||
            cli.isVersionHelpRequested) return

    input = container.input!!.toAbsolutePath().normalize()
    compress = container.compress
    texturesDirectory = container.texturesDirectory ?: input.parent ?: Path.of(".")
    info = container.info
    background = container.background!!
    faceCulling = container.faceCulling
    light = container.light
    scale = container.scale
    planes = container.planes
    acceleration = container.acceleration
    sensitivity = container.sensitivity

    Application.launch(ModelViewer::class.java)
}
