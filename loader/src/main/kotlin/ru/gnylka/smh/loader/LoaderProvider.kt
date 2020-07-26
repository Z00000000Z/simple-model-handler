package ru.gnylka.smh.loader

import ru.gnylka.smh.processing.input.InputModel
import java.nio.file.Path

/**
 * Interface that must be implemented by loaders
 */
interface LoaderProvider {

    /**
     * A stateless set of extensions of files this loader can load (without dot, e.g. "fbx")
     */
    val fileExtensions: Set<String>

    /**
     * Called to load [model][InputModel] from specified file path
     *
     * @param filePath a file with any extension specified in [fileExtensions]
     *
     * @throws LoadingException if any exception occured while loading model.
     * This exception can be caught in order to try to load model from this file by other loader,
     * whereas other exceptions _won't be caught_
     *
     * @return loaded model
     */
    @Throws(LoadingException::class)
    fun loadFrom(filePath: Path): InputModel

}
