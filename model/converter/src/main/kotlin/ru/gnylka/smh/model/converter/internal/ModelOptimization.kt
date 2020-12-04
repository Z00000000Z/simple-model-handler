package ru.gnylka.smh.model.converter.internal

import ru.gnylka.smh.model.data.SimpleModel
import ru.gnylka.smh.processing.data.MeshAttribute.*
import java.util.*
import kotlin.collections.ArrayList

internal class ModelOptimization internal constructor(
        private val simpleModel: SimpleModel,
        private val optimizePoints: Boolean = true,
        private val indexPoints: Boolean = true,
        private val optimizeNormals: Boolean = true,
        private val indexNormals: Boolean = true,
        private val optimizeTexCoords: Boolean = true,
        private val indexTexCoords: Boolean = true,
        private val optimizeParts: Boolean = true
) {

    private val MIN_REPEAT_COUNT = 3
    private val MIN_POINT_REPEAT_COUNT = MIN_REPEAT_COUNT + 1
    private val MIN_TEX_COORD_REPEAT_COUNT = MIN_REPEAT_COUNT + 1

    private val points = simpleModel.points
    private lateinit var newPoints: MutableList<Float>
    // keep pointsIndices sorted for faster loading using binary search
    private val pointsIndices = sortedMapOf<Float, List<Float>>()

    private val normals = simpleModel.normals
    private lateinit var newNormals: MutableList<Float>
    private val normalsIndices = mutableListOf<List<Float>>()

    private val texCoords = simpleModel.texCoords
    private lateinit var newTexCoords: MutableList<Float>
    // keep texCoordsIndices sorted for faster loading using binary search
    private val texCoordsIndices = sortedMapOf<Float, List<Float>>()

    private val random = Random()

    internal fun optimizeModel(): SimpleModel {
        // avoid boxing if everything is skipped
        if (isSkipAll()) return simpleModel

        newPoints =
                if (optimizePoints) optimizePoints()
                else points.toMutableList()
        if (indexPoints) index(newPoints, POSITION.size,
                MIN_POINT_REPEAT_COUNT, pointsIndices)

        newNormals =
                if (optimizeNormals) optimizeNormals()
                else normals.toMutableList()
        if (indexNormals) indexNormals(newNormals, NORMAL.size, normalsIndices)

        newTexCoords =
                if (optimizeTexCoords) optimizeTexCoords()
                else texCoords.toMutableList()
        if (indexTexCoords) index(newTexCoords, TEXCOORD.size,
                MIN_TEX_COORD_REPEAT_COUNT, texCoordsIndices)

        val newParts =
                if (optimizeParts) simpleModel.parts.map(::optimizeFaces)
                else simpleModel.parts.toList()

        return SimpleModel(
                newPoints.toFloatArray(),
                pointsIndices.keys.toFloatArray(),
                pointsIndices.values.flatten().toFloatArray(),
                simpleModel.pointsCount,

                newNormals.toFloatArray(),
                normalsIndices.flatten().toFloatArray(),
                simpleModel.normalsCount,

                newTexCoords.toFloatArray(),
                texCoordsIndices.keys.toFloatArray(),
                texCoordsIndices.values.flatten().toFloatArray(),
                simpleModel.texCoordsCount,

                newParts.toTypedArray(),
                simpleModel.facesCount,

                simpleModel.materials,
                simpleModel.nodes,
                simpleModel.globalProperties
        )
    }

    private fun isSkipAll() = !optimizePoints &&
            !indexPoints &&
            !optimizeNormals &&
            !indexNormals &&
            !optimizeTexCoords &&
            !indexTexCoords &&
            !optimizeParts

    /*
        X, Y, Z
        X1, Y1, Z1

        If X1 == X && Y1 == Y && Z1 == Z, replaces X1, Y1, Z1 with Float.NaN

        If <number> is presented in pointsKeys array, then 3 values with indices x, x + 1, x + 2,
        where x is the index of <number> in pointsKeys array, must be taken from indices array
     */
    private fun optimizePoints(): MutableList<Float> {
        val newPoints = ArrayList<Float>(points.size)
        var previous = floatArrayOf(Float.NaN, Float.NaN, Float.NaN)

        for (i in points.indices step POSITION.size) {
            val current = points.sliceArray(i until i + POSITION.size)

            if (current contentEquals previous) newPoints.add(Float.NaN)
            else {
                newPoints += current.toTypedArray()
                previous = current
            }
        }

        return newPoints
    }

    /*
        X, Y, Z
        X1, Y1, Z1

        If X1 == X && Y1 == Y && Z1 == Z, replaces X1, Y1, Z1 with Float.NaN

        Number higher than 1.0 indicates that previous 3 values must be repeated <number> times

        Number <= -2.0 indicates that 3 values with indices x, x + 1, x + 2,
        where x is abs(<number>) - 2, must be taken from indices array
     */
    private fun optimizeNormals(): MutableList<Float> {
        val newNormals = ArrayList<Float>(normals.size)
        var previous = listOf(Float.NaN, Float.NaN, Float.NaN)
        var equalsPreviousCount = 0

        for (i in normals.indices step NORMAL.size) {
            val current = normals.slice(i until i + NORMAL.size)

            if (current == previous) equalsPreviousCount++
            else {
                writeDataRepeatedCount(newNormals, equalsPreviousCount)
                equalsPreviousCount = 0
                newNormals += current
                previous = current
            }
        }

        writeDataRepeatedCount(newNormals, equalsPreviousCount)
        return newNormals
    }

    /*
        X, Y
        X1, Y1

        If X1 == X && Y1 == Y, replaces X1, Y1 with Float.NaN

        Number higher than 1.0 indicates that previous 2 values must be repeated <number> times

        Number <= -2.0 indicates that 2 values with indices x, x + 1,
        where x is abs(<number>) - 2, must be taken from indices array
     */
    private fun optimizeTexCoords(): MutableList<Float> {
        val newTexCoords = ArrayList<Float>(texCoords.size)
        var previous = listOf(Float.NaN, Float.NaN)
        var equalsPreviousCount = 0

        for (i in texCoords.indices step TEXCOORD.size) {
            val current = texCoords.slice(i until i + TEXCOORD.size)
            if (current == previous) equalsPreviousCount++
            else {
                writeDataRepeatedCount(newTexCoords, equalsPreviousCount)
                equalsPreviousCount = 0
                newTexCoords += current
                previous = current
            }
        }

        writeDataRepeatedCount(newTexCoords, equalsPreviousCount)
        return newTexCoords
    }

    private fun index(
            values: MutableList<Float>,
            groupSize: Int,
            minRepeatCount: Int,
            indicesDest: MutableMap<Float, List<Float>>
    ) {
        val usedValues = mutableListOf<Float>()
        usedValues.addAll(values)
        val indices = getRepeatedValues(values, groupSize) { !it.isNaN() }
                .filterValues { it >= minRepeatCount }
                .keys.map {
                    val newUniqueValue = generateUniqueValue(usedValues)
                    usedValues += newUniqueValue
                    newUniqueValue to it
                }.toMap(indicesDest)
        val indicesKeys = indices.keys

        indices.forEach { (replaceValue, valuesGroup) ->
            replaceAllSubLists(values, valuesGroup, replaceValue) {
                !it.isNaN() && it !in indicesKeys
            }
        }
    }

    private fun indexNormals(
            values: MutableList<Float>,
            valuesGroupSize: Int,
            indexDestination: MutableList<List<Float>>
    ) {
        val isNormal = { value: Float -> value in -1f..1f }

        getRepeatedValues(values, valuesGroupSize, isNormal)
                .filterValues { it >= MIN_REPEAT_COUNT }
                .keys.also { indexDestination += it }
                .forEachIndexed { i, subList ->
                    replaceAllSubLists(values, subList, -(i + 2).toFloat(), isNormal)
                }
    }

    private fun writeDataRepeatedCount(container: MutableList<Float>, repeatedCount: Int) {
        if (repeatedCount == 1) container += Float.NaN
        else if (repeatedCount > 1) container += repeatedCount.toFloat()
    }

    /*
        Negative number indicates than last number is repeated <abs(number)> times
        with progression 1
        If negative number is the first number, 0 is the beginning number
     */
    private fun optimizeFaces(faces: ShortArray): ShortArray {
        val newFaces = ArrayList<Short>(faces.size)
        var previous = -1

        for (value in faces) {
            if (value - 1 != previous) {
                addProgression(newFaces, previous)
                newFaces += value
            }
            previous = value.toInt()
        }
        addProgression(newFaces, previous)

        return newFaces.toShortArray()
    }

    private fun addProgression(faces: MutableList<Short>, previousValue: Int) {
        val diff =
                if (faces.isEmpty()) previousValue + 1
                else previousValue - faces.last()
        if (diff > 0)
            faces.add((-diff).toShort())
    }

    /*
        List<Float> - group of values (size == valuesGroupSize)
        Int - the amount of same groups found in values
     */
    private fun getRepeatedValues(
            values: List<Float>,
            valuesGroupSize: Int,
            isNormalValue: (Float) -> Boolean
    ): Map<List<Float>, Int> {
        val repeatedValues = hashMapOf<List<Float>, Int>()
        var i = 0
        while (i < values.size)
            if (!isNormalValue(values[i])) i++
            else {
                val valuesSubList = values.slice(i until i + valuesGroupSize)
                repeatedValues.merge(valuesSubList, 1) { oldValue, _ -> oldValue + 1 }
                i += valuesGroupSize
            }

        return repeatedValues
    }

    private fun generateUniqueValue(usedValues: List<Float>): Float {
        var uniqueValue: Float
        do {
            uniqueValue = random.nextFloat() * Short.MAX_VALUE
        } while (uniqueValue in usedValues)
        return uniqueValue
    }

    /*
        This function splits source into several subLists with the same size as subList
        But it takes into account abnormal values through the use of isNormalValue predicate

        For instance, tex coords array looks like this:
        1. listOf(0.5f, 0.5f)
        2. listOf(Float.NaN )
        3. listOf(1f,   0.7f)
        4. listOf(0.8f, 0.1f)

        If subList is listOf(1f, 0.7f), then 3rd list will be replaced with replaceWith
     */
    private fun replaceAllSubLists(
            source: MutableList<Float>,
            subList: List<Float>,
            replaceWith: Float,
            isNormalValue: (Float) -> Boolean
    ) {
        var i = 0
        while (i < source.size) {
            if (!isNormalValue(source[i])) i++
            else if (source.slice(i until i + subList.size) == subList) {
                source[i] = replaceWith
                repeat(subList.size - 1) {
                    source.removeAt(i + 1)
                }
                i++
            } else i += subList.size
        }
    }

}
