package ru.gnylka.smh.loader.fbxconv

import ru.gnylka.smh.loader.LoaderProvider
import ru.gnylka.smh.loader.g3dj.G3djLoaderProvider
import ru.gnylka.smh.processing.input.InputModel
import java.nio.file.Files
import java.nio.file.Path

class FbxConvLoaderProvider : LoaderProvider {

    private val doFlipTexCoords = System.getProperty(
            "ru.gnylka.smh.loader.fbxconv.flipTextureCoordinates", "true")!!.toBoolean()

    override val fileExtensions = setOf("fbx", "dae", "obj")

    override fun loadFrom(filePath: Path): InputModel {
        val wrapper = FbxConvWrapper(filePath.toString(), doFlipTexCoords)
        val resultG3dj = wrapper.startConverter()
        try {
            return G3djLoaderProvider().loadFrom(resultG3dj)
        } finally {
            Files.deleteIfExists(resultG3dj)
        }
    }

}
