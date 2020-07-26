package ru.gnylka.smh.model.converter.cli

import ru.gnylka.smh.loader.loadModelFrom
import ru.gnylka.smh.model.converter.convertModel
import ru.gnylka.smh.model.converter.optimizeModel
import ru.gnylka.smh.model.converter.writeModel
import ru.gnylka.smh.model.data.SimpleModel
import ru.gnylka.smh.processing.IllegalPropertyException
import ru.gnylka.smh.processing.ProcessingException
import ru.gnylka.smh.processing.ProcessingPluginException
import ru.gnylka.smh.processing.processModel
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*
import java.util.zip.GZIPOutputStream

fun operateOn(data: SessionData): Exception? =
        try {
            operate(data)
            null
        } catch (e: NoSuchFileException) {
            data.logger.warning(LOG_FILE_NOT_FOUND.format(e.message))
            e
        } catch (e: ProcessingException) {
            data.logger.warning(LOG_PROCESSING_EXCEPTION.format(e.message))
            e
        } catch (e: IllegalPropertyException) {
            data.logger.warning(LOG_ILLEGAL_PROPERTY.format(e.message))
            e
        } catch (e: ProcessingPluginException) {
            data.logger.warning(LOG_PROCESSING_PLUGIN_EXCEPTION.format(e.pluginName, e.message))
            e
        } catch (e: FileAlreadyExistsException) {
            data.logger.warning(LOG_FILE_ALREADY_EXISTS.format(e.message))
            e
        } catch (e: IOException) {
            data.logger.warning(LOG_UNKNOWN_IO_EXCEPTION.format(e))
            e
        } catch (e: Exception) {
            data.logger.warning(LOG_UNKNOWN_EXCEPTION.format(e))
            e
        }

private fun operate(data: SessionData) {
    val inputModel = loadModelFrom(data.fromFile)
    data.logger.fine(LOG_LOADED_MODEL.format(absoluteNormalized(data.fromFile)))

    val model = processModel(
            inputModel,
            data.propertyPrefix,
            data.skipInvalidProperties,
            { it in data.usePlugins },
            data.pluginArguments
    )

    var simpleModel = convertModel(model, data.pickFirstTexture)
    data.logger.fine(LOG_CONVERTED_MODEL.format(data.modelName))

    if (data.skipNormals)
        simpleModel = removeNormals(simpleModel)

    val opts = data.optimize
    val optimizedModel = optimizeModel(
            simpleModel,
            OptimizationOptions.OPTIMIZE_POINTS in opts,
            OptimizationOptions.INDEX_POINTS in opts,
            OptimizationOptions.OPTIMIZE_NORMALS in opts,
            OptimizationOptions.INDEX_NORMALS in opts,
            OptimizationOptions.OPTIMIZE_TEX_COORDS in opts,
            OptimizationOptions.INDEX_TEX_COORDS in opts,
            OptimizationOptions.OPTIMIZE_PARTS in opts
    )

    if (data.skipNormals || opts.isNotEmpty())
        data.logger.fine(LOG_OPTIMIZED_MODEL.format(data.modelName))

    createFileOutput(data.toFile, data.compress).use {
        writeModel(optimizedModel, it, data.isBinary)
    }
    data.logger.fine(LOG_WROTE_MODEL.format(absoluteNormalized(data.toFile)))
}

private fun removeNormals(model: SimpleModel) = model.run {
    val empty = floatArrayOf()
    SimpleModel(
            points, pointsKeys, pointsIndices, pointsCount,
            empty, empty, 0,
            texCoords, texCoordsIndices, texCoordsCount,
            parts, facesCount,
            materials, nodes, globalProperties
    )
}

private fun createFileOutput(toFile: Path, compress: Boolean): OutputStream {
    if (Files.notExists(toFile.parent))
        Files.createDirectories(toFile.parent)

    var output: OutputStream =
            Files.newOutputStream(toFile,
                    WRITE,
                    CREATE,
                    TRUNCATE_EXISTING
            ).buffered()

    if (compress) output = GZIPOutputStream(output)

    return output
}

private fun absoluteNormalized(path: Path) = path.toAbsolutePath().normalize()
