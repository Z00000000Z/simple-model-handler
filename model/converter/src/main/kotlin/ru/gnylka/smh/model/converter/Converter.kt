package ru.gnylka.smh.model.converter

import ru.gnylka.smh.model.converter.internal.BinaryWriting
import ru.gnylka.smh.model.converter.internal.Data2Simple
import ru.gnylka.smh.model.converter.internal.ModelOptimization
import ru.gnylka.smh.model.converter.internal.TextWriting
import ru.gnylka.smh.model.data.SimpleModel
import ru.gnylka.smh.processing.data.Model
import java.io.OutputStream

/**
 * Converts [Model] to [SimpleModel]
 *
 * @param model model to convert
 * @param pickFirstTexture if model has materials with multiple textures of same type,
 * pick the first one (otherwise throw [IllegalArgumentException])
 *
 * @author Z00000000Z
 */
fun convertModel(
        model: Model,
        pickFirstTexture: Boolean = false
) = Data2Simple(model, pickFirstTexture).createSimpleModel()

/**
 * Optimizes the specified model
 *
 * There are 7 optimization methods, each can be enabled or disabled (by default all are enabled):
 *
 * ## Points optimization
 *
 * If model contains 2 points (each is 3 values):
 *
 *      X   Y   Z
 *      X1  Y1  Z1   <- replace with Float.NaN
 * Where X == X1 && Y == Y1 && Z == Z1, replace X1, Y1, Z1 with [Float.NaN]
 *
 * ## Points indexing
 *
 * If the same point is encountered more than [ModelOptimization.MIN_POINT_REPEAT_COUNT] times in
 * [SimpleModel.points], then generate a unique key
 * (key is essentially a float value, which [SimpleModel.points] does not contain),
 * replace point's values with this key and put key to [SimpleModel.pointsKeys]
 * and point's values to [SimpleModel.pointsIndices]
 *
 *      5.1 6.4 3.4
 *      6.3 6.0 8.8   <- replace with 6.7 for instance
 *      9.0 8.9 4.7
 *      6.3 6.0 8.8   <- replace with 6.7 for instance
 *
 * ## Normals optimization
 *
 * If model contains 2 normals (each is 3 values):
 *
 *      X   Y   Z
 *      X1  Y1  Z1   <- replace with Float.NaN
 * Where X == X1 && Y == Y1 && Z == Z1, replace X1, Y1, Z1 with [Float.NaN]
 *
 * If the following X2, Y2, Z2 equal to X1, Y1, Z1, then replace X1, Y1, Z1, X2, Y2, Z2 with 2.0
 * and so on
 *
 *      X   Y   Z
 *      X1  Y1  Z1   | replace with 2.0
 *      X2  Y2  Z2   |
 *
 * ## Normals indexing
 *
 * If the same normal is encountered more than [ModelOptimization.MIN_REPEAT_COUNT] times in
 * [SimpleModel.normals], than add it to [ModelOptimization.normalsIndices] and replace with x
 *
 * Where x = -(normalsIndices's previous size / 3 + 2)
 *
 *      0.3 1.0 0.8
 *      0.8 0.7 0.1   <- replace with x (-2.0)
 *      0.9 0.0 0.5
 *      0.8 0.7 0.1   <- replace with x (-2.0)
 *
 * ## Texture coordinates optimization
 *
 * If model contains 2 texture coordinates (each 2 values):
 *
 *      X   Y
 *      X1  Y1
 * Where X == X1 && Y == Y1, replace X1, Y1 with [Float.NaN]
 *
 * If the following X2, Y2 equal to X1, Y1, then replace X1, Y1, X2, Y2 with 2.0 and so on
 *
 * ## Texture coordinates indexing
 *
 * If the same texture coordinate is encountered more than [ModelOptimization.MIN_REPEAT_COUNT]
 * times in [ModelOptimization.texCoords], then add it to [ModelOptimization.texCoordsIndices]
 * and replace with x
 *
 * Where x = -(texCoordsIndices's previous size / 2 + 2)
 *
 *      0.8 0.9 0.1
 *      0.9 0.4 0.4   <- replace with x (-2.0)
 *      0.2 0.9 0.6
 *      0.9 0.4 0.4   <- replace with x (-2.0)
 *
 * ## Parts optimization
 *
 * If part contains 6 indices:
 *
 *      1   2   3
 *      4   5   6
 * Then they can be replaced with 1, -5 (5 times from 1 with progression 1)
 *
 * If the first index is negative number, then 0 is the beginning of progression
 *
 * @param simpleModel model to optimize
 * @param optimizePoints use points optimization
 * @param indexPoints use points indexing.
 * Note: points indexing can seriously slow down model loading. Use with caution!
 * @param optimizeNormals use normals optimization
 * @param indexNormals use normals indexing
 * @param optimizeTexCoords use texture coordinates optimization
 * @param indexTexCoords use texture coordinates indexing
 * @param optimizeParts use parts optimization
 *
 * @return a new model with optimizations applied
 *
 * @author Z00000000Z
 */
fun optimizeModel(
        simpleModel: SimpleModel,
        optimizePoints: Boolean = true,
        indexPoints: Boolean = true,
        optimizeNormals: Boolean = true,
        indexNormals: Boolean = true,
        optimizeTexCoords: Boolean = true,
        indexTexCoords: Boolean = true,
        optimizeParts: Boolean = true
) = ModelOptimization(
        simpleModel,
        optimizePoints, indexPoints,
        optimizeNormals, indexNormals,
        optimizeTexCoords, indexTexCoords,
        optimizeParts
).optimizeModel()

/**
 * Writes model to the output stream (the stream is not closed)
 *
 * @param simpleModel model to write
 * @param output stream to write model to
 * @param isBinary whether to use binary format or text
 * @param indent a string used for indenting text output
 *
 * @author Z00000000Z
 */
fun writeModel(
        simpleModel: SimpleModel,
        output: OutputStream,
        isBinary: Boolean = true,
        indent: String = "  "
) {
    if (isBinary) BinaryWriting(simpleModel).writeModelAsBinary(output)
    else TextWriting(simpleModel, indent).writeModelAsText(output)
}
