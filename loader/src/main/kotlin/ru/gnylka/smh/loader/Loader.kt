package ru.gnylka.smh.loader

import ru.gnylka.smh.processing.input.InputModel
import java.nio.file.Path
import java.util.*

private const val MODEL_FILE_WITHOUT_EXTENSION = "Cannot load model from file without extension"
private const val UNABLE_TO_FIND_LOADER = "Unable to find loader for %s extension"
private const val FAILED_TO_LOAD_MODEL = "Failed to load model from %s"

/**
 * Loads [InputModel] from specified [filePath] using provided [LoaderProvider]s
 *
 * @param filePath file path to load model from
 *
 * @throws LoadingException with providers' suppressed exceptions
 *
 * @author Z00000000Z
 */
fun loadModelFrom(filePath: Path): InputModel {
    val fileName = filePath.fileName.toString()
    check('.' in fileName) { MODEL_FILE_WITHOUT_EXTENSION }

    val extension = fileName.substringAfterLast('.')
    val loaders = getAvailableLoaders().filter { extension in it.fileExtensions }

    if (loaders.isEmpty())
        throw LoadingException(UNABLE_TO_FIND_LOADER.format(extension))

    var inputModel: InputModel? = null
    val exceptions = mutableListOf<Throwable>()

    for (loader in loaders) try {
        inputModel = loader.loadFrom(filePath)
        break
    } catch (e: LoadingException) {
        exceptions += e
    }

    if (inputModel == null) {
        val exception = LoadingException(FAILED_TO_LOAD_MODEL.format(filePath))
        exceptions.forEach(exception::addSuppressed)
        throw exception
    }

    return inputModel
}

/**
 * Searches for available loaders using [ServiceLoader] and [LoaderProvider]
 *
 * @see ServiceLoader
 *
 * @author Z00000000Z
 */
fun getAvailableLoaders(): List<LoaderProvider> =
        ServiceLoader.load(LoaderProvider::class.java).toList()
