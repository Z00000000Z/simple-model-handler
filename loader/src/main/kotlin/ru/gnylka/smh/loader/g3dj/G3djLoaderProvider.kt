package ru.gnylka.smh.loader.g3dj

import com.google.gson.Gson
import ru.gnylka.smh.loader.LoaderProvider
import ru.gnylka.smh.processing.input.InputModel
import java.nio.file.Files
import java.nio.file.Path

class G3djLoaderProvider : LoaderProvider {

    private val gson = Gson()

    override val fileExtensions = setOf("g3dj")

    override fun loadFrom(filePath: Path): InputModel {
        val g3djModel = Files.newBufferedReader(filePath).use {
            gson.fromJson(it, G3djModel::class.java)
        }
        return G3dj2Input(g3djModel).createInputModel()
    }

}
