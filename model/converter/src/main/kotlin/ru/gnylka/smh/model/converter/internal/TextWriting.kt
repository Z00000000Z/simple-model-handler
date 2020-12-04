package ru.gnylka.smh.model.converter.internal

import ru.gnylka.smh.model.data.*
import ru.gnylka.smh.processing.data.MeshAttribute.*
import ru.gnylka.smh.utils.*
import java.io.OutputStream
import java.io.PrintWriter
import kotlin.math.abs
import kotlin.math.max

internal class TextWriting internal constructor(
        private val simpleModel: SimpleModel,
        private val indent: String
) {

    private enum class PreviousValue {
        COMMON, REPEATED, INDEXED
    }

    private val sb = StringBuilder()

    private val maxPoint = maxFloatLength(simpleModel.points + simpleModel.pointsIndices)
    private val maxPointKey = maxFloatLength(simpleModel.pointsKeys, positive = true)
    private val maxNormal = maxFloatLength(simpleModel.normals + simpleModel.normalsIndices)
    private val maxNormalKey = max(0, simpleModel.normalsIndices.size / NORMAL.size - 1)
            .toString().length
    private val maxTexCoord = maxFloatLength(simpleModel.texCoords + simpleModel.texCoordsIndices)
    private val maxTexCoordKey = maxFloatLength(simpleModel.texCoordsKeys, positive = true)

    internal fun writeModelAsText(output: OutputStream) {
        val writer = PrintWriter(output)
        val data = simpleModel.getData()
        writer.println(data)
        writer.flush()
    }

    private fun SimpleModel.getData(): String {
        sb.clear()

        writeGlobalProperties()

        writePointsIndices()
        writeNormalsIndices()
        writeTexCoordsIndices()

        writePoints()
        writeNormals()
        writeTexCoords()

        writeParts()
        writeMaterials()
        writeNodes()

        return sb.toString()
    }

    private fun SimpleModel.writeGlobalProperties() {
        sb + "Global properties (size: " + globalProperties.size += "):"
        sb + getProperties(globalProperties).indentBy(indent)
        sb.appendLine()
    }

    private fun SimpleModel.writePointsIndices() {
        sb + "Points indices (size: " + pointsKeys.size += "):"

        val l = maxPoint
        for (i in pointsKeys.indices)
            sb.appendf("%s%-${maxPointKey}.6f = [% ${l}.6f, % ${l}.6f, % ${l}.6f]%n",
                    indent, pointsKeys[i],
                    pointsIndices[i * 3 + 0],
                    pointsIndices[i * 3 + 1],
                    pointsIndices[i * 3 + 2])

        sb.appendLine()
    }

    private fun SimpleModel.writeNormalsIndices() {
        val size = normalsIndices.size / NORMAL.size
        sb + "Normals indices (size: " + size += "):"

        val l = maxNormal
        for (i in 0 until size)
            sb.appendf("%s%-${maxNormalKey}i = [% ${l}.6f, % ${l}.6f, % ${l}.6f]%n",
                    indent, i,
                    normalsIndices[i * 3 + 0],
                    normalsIndices[i * 3 + 1],
                    normalsIndices[i * 3 + 2])

        sb.appendLine()
    }

    private fun SimpleModel.writeTexCoordsIndices() {
        sb + "Texture coordinates indices (size: " + texCoordsIndices.size += "):"

        val l = maxTexCoord
        for (i in texCoordsKeys.indices)
            sb.appendf("%s%-${maxTexCoordKey}.6f = [% ${l}.6f, % ${l}.6f]%n",
                    indent, texCoordsKeys[i],
                    texCoordsIndices[i * 2 + 0],
                    texCoordsIndices[i * 2 + 1])

        sb.appendLine()
    }

    private fun SimpleModel.writePoints() {
        sb + "Points (size: " + pointsCount + ", values: " + pointsCount * POSITION.size += "):"
        sb += writeArray(points, POSITION.size, maxPoint, maxPointKey,
                { value -> if (value.isNaN()) 1 else 0 },
                { value -> if (pointsKeys.any { it == value }) value else Float.NaN }
        )
    }

    private fun SimpleModel.writeNormals() {
        sb + "Normals (size: " + normalsCount + ", values: " + normalsCount * NORMAL.size += "):"
        sb += writeArray(normals, NORMAL.size, maxNormal, maxNormalKey, { value ->
            if (value.isNaN()) 1
            else if (value > 1.0) value.toInt()
            else 0
        }, { value ->
            if (value < -1.0) abs(value) - 2
            else Float.NaN
        })
    }

    private fun SimpleModel.writeTexCoords() {
        sb + "Texture coordinates (size: " + texCoordsCount + ", values: " +
                texCoordsCount * TEXCOORD.size += "):"
        sb += writeArray(texCoords, TEXCOORD.size, maxTexCoord, maxTexCoordKey,
                { value -> if (value.isNaN()) 1 else 0 },
                { value -> if (texCoordsKeys.any { it == value }) value else Float.NaN }
        )
    }

    private fun SimpleModel.writeParts() {
        sb + "Parts (size: " + facesCount.size += "):"
        for ((i, count) in facesCount.withIndex()) {
            val str = "Part[$i] (size: $count, values: ${count * 3}):\n" +
                    getPartFaces(parts[i]).indentBy(indent)
            sb + str.indentBy(indent)

        }
        sb.appendLine()
    }

    private fun getPartFaces(partFaces: ShortArray): String {
        if (partFaces.isEmpty()) return "\n"

        val strB = StringBuilder()
        val indexLen = getIndexLength(partFaces)
        val formatStr = "% ${indexLen}d"

        var offset = 0
        var previousNegative = false

        for ((i, value) in partFaces.withIndex()) {
            val rem = (i + offset) % 3
            if (value < 0) {
                previousNegative = true
                if (rem != 0) strB.appendLine()
                strB + formatStr.format(value) += ','
                offset += -value - 1
                offset %= 3
            } else {
                if (previousNegative) {
                    // indexLen - the length of the string of the biggest index
                    // + 1 because in "% ${indexLen}d" a whitespace is used
                    // + 1 for additional whitespace that replaces comma
                    strB + " ".repeat((indexLen + 2) * (rem))
                    previousNegative = false
                }

                strB + formatStr.format(value) + ',' +
                        if (rem == 2) '\n'
                        else ' '
            }
        }

        if (partFaces.isNotEmpty() && !previousNegative)
            strB.removeLast(",")

        return strB.toString()
    }

    private fun SimpleModel.writeMaterials() {
        sb + "Materials (size: " + materials.size += "):"
        for ((i, material) in materials.withIndex()) {
            val str = "Material[$i]:\n" + material.getMaterial().indentBy(indent)
            sb + str.indentBy(indent)
        }
        sb.appendLine()
    }

    private fun SimpleMaterial.getMaterial(): String {
        val strB = StringBuilder()
        strB + "Ambient: " += ambient.joinToString(", ", "[", "]")
        strB + "Diffuse: " += diffuse.joinToString(", ", "[", "]")
        strB + "Specular: " += specular.joinToString(", ", "[", "]")
        strB + "Opacity: " += opacity
        strB + "Shininess: " += shininess
        strB + "Diffuse texture: " +=
                if (diffuseTexture.isEmpty()) "<none>"
                else diffuseTexture
        strB + "Specular texture: " +=
                if (specularTexture.isEmpty()) "<none>"
                else specularTexture
        return strB.toString()
    }

    private fun SimpleModel.writeNodes() {
        sb + "Nodes (size: " + nodes.size += "):"
        for ((i, node) in nodes.withIndex()) {
            val str = "Node[$i]:\n" + node.getNode().indentBy(indent)
            sb + str.indentBy(indent)
        }
        sb.appendLine()
    }

    private fun SimpleNode.getNode(): String {
        val strB = StringBuilder()

        strB + "ID: " += id

        strB + "Translation: " += translation.joinToString(", ", "[", "]")
        strB + "Rotation: " += rotation.joinToString(", ", "[", "]")
        strB + "Scale: " += scale.joinToString(", ", "[", "]")

        strB + "Properties (size: " + properties.size += "):"
        strB + getProperties(properties).indentBy(indent)

        strB + "Parts (size: " + nodeParts.size += "):"
        for ((i, nodePart) in nodeParts.withIndex()) {
            val str = "Part[$i]:\n" + nodePart.getNodePart().indentBy(indent)
            strB + str.indentBy(indent)
        }

        strB + "Children (size: " + children.size += "):"
        for ((i, child) in children.withIndex()) {
            val str = "Child[$i]:\n" + child.getNode().indentBy(indent)
            strB + str.indentBy(indent)
        }

        return strB.toString()
    }

    private fun SimpleNodePart.getNodePart(): String {
        val strB = StringBuilder()
        strB + "Part index: " += partIndex
        strB + "Material index: " += materialIndex
        return strB.toString()
    }

    private fun getProperties(properties: Array<String>): String {
        val strB = StringBuilder()
        properties.forEachIndexed { i, prop ->
            strB + '[' + i + "] " += prop
        }
        return strB.toString()
    }

    private fun getIndexLength(array: ShortArray) = array.run {
        if (isEmpty()) 1
        else max("% 1d".format(minOrNull()!!).length,
                "% 1d".format(maxOrNull()!!).length)
    }

    private fun writeArray(
            array: FloatArray,
            valuesPerLine: Int,
            maxLength: Int,
            maxKeyLength: Int,
            isRepeated: (Float) -> Int,
            isIndexed: (Float) -> Float
    ): String {
        val strB = StringBuilder()
        var offset = 0
        var previous = PreviousValue.COMMON

        for ((i, value) in array.withIndex()) {
            val repeated = isRepeated(value)
            if (repeated > 0) {
                previous = PreviousValue.REPEATED
                strB + "Repeat " + repeated += " times..."
                offset += valuesPerLine - 1
                offset %= valuesPerLine
                continue
            }

            val indexed = isIndexed(value)
            if (!indexed.isNaN()) {
                previous = PreviousValue.INDEXED
                strB += "[%-${maxKeyLength}.6f]".format(indexed)
                offset += valuesPerLine - 1
                offset %= valuesPerLine
            } else {
                previous = PreviousValue.COMMON
                val isLast = (i + offset) % valuesPerLine == valuesPerLine - 1
                strB + "% ${maxLength}.6f".format(value) + ',' +
                        if (isLast) '\n'
                        else ' '
            }
        }

        if (array.isNotEmpty() && previous == PreviousValue.COMMON)
            strB.removeLast(",")

        return strB.toString()
    }

    // ----- Help functions ----- //

    private fun StringBuilder.removeLast(subString: String) {
        val lastSubStrIndex = lastIndexOf(subString)
        delete(lastSubStrIndex, lastSubStrIndex + subString.length)
    }

    private fun maxFloatLength(floats: FloatArray, positive: Boolean = false): Int {
        val formatStr = if (positive) "%1.6f" else "% 1.6f"
        return floats.map { formatStr.format(it).length }
                .maxOrNull()
                ?: if (positive) 3 else 4
    }

}
