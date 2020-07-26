package ru.gnylka.smh.model.converter.internal

import ru.gnylka.smh.model.data.SimpleMaterial
import ru.gnylka.smh.model.data.SimpleModel
import ru.gnylka.smh.model.data.SimpleNode
import ru.gnylka.smh.model.data.SimpleNodePart
import ru.gnylka.smh.processing.data.MeshAttribute.*
import ru.gnylka.smh.utils.appendf
import ru.gnylka.smh.utils.indentBy
import ru.gnylka.smh.utils.plus
import ru.gnylka.smh.utils.plusAssign
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

    private val maxPointKey = simpleModel.pointsKeys.lastOrNull()
            ?.let { "%f".format(it).length } ?: 3

    private val maxPointIndex = maxFloatLength(simpleModel.pointsIndices)

    private val maxNormalIndex = (simpleModel.normalsIndices.size / NORMAL.size)
            .toString().length

    private val maxTexCoordIndex = (simpleModel.texCoordsIndices.size / TEXCOORD.size)
            .toString().length

    internal fun writeModelAsText(output: OutputStream) {
        val writer = PrintWriter(output)
        val data = simpleModel.getData()
        writer.println(data)
        writer.flush()
    }

    private fun SimpleModel.getData(): String {
        sb.setLength(0)

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

        val str = sb.toString()
        sb.clear()
        return str
    }

    private fun SimpleModel.writeGlobalProperties() {
        sb + "Global properties (size: " + globalProperties.size += "):"
        sb + getProperties(globalProperties).indentBy(indent)
        sb.appendln()
    }

    private fun SimpleModel.writePointsIndices() {
        sb + "Points indices (size: " + pointsKeys.size += "):"

        val k = maxPointKey
        val l = maxPointIndex

        for (i in pointsKeys.indices)
            sb.appendf("%s%-${k}f = [%${l}f, %${l}f, %${l}f]%n",
                    indent, pointsKeys[i],
                    pointsIndices[i * 3 + 0],
                    pointsIndices[i * 3 + 1],
                    pointsIndices[i * 3 + 2])

        sb.appendln()
    }

    private fun SimpleModel.writeNormalsIndices() {
        val size = normalsIndices.size / 3
        sb + "Normals indices (size: " + size += "):"

        for (i in 0 until size)
            sb.appendf("%s[%-${maxNormalIndex}d] = [%-7.5f, %-7.5f, %-7.5f]%n",
                    indent, i,
                    normalsIndices[i * 3 + 0],
                    normalsIndices[i * 3 + 1],
                    normalsIndices[i * 3 + 2])

        sb.appendln()
    }

    private fun SimpleModel.writeTexCoordsIndices() {
        val size = texCoordsIndices.size / 2
        sb + "Texture coordinates indices (size: " + size += "):"

        for (i in 0 until size)
            sb.appendf("%s[%-${maxTexCoordIndex}d] = [%-7.5f, %-7.5f]%n",
                    indent, i,
                    texCoordsIndices[i * 2 + 0],
                    texCoordsIndices[i * 2 + 1])

        sb.appendln()
    }

    private fun SimpleModel.writePoints() {
        sb + "Points (size: " + pointsCount + ", values: " + pointsCount * POSITION.size += "):"
        sb += writeArray(points, POSITION.size, maxPointKey,
                { if (it.isNaN()) 1 else 0 },
                { if (it in pointsKeys) it else Float.NaN }
        )
    }

    private fun SimpleModel.writeNormals() {
        sb + "Normals (size: " + normalsCount + ", values: " + normalsCount * NORMAL.size += "):"
        sb += writeArray(normals, NORMAL.size, -1, {
            if (it.isNaN()) 1
            else if (it > 1.0) it.toInt()
            else 0
        }, {
            if (it < -1.0) abs(it) - 2
            else Float.NaN
        })
    }

    private fun SimpleModel.writeTexCoords() {
        sb + "Texture coordinates (size: " + texCoordsCount + ", values: " +
                texCoordsCount * TEXCOORD.size += "):"
        sb += writeArray(texCoords, TEXCOORD.size, -1, {
            if (it.isNaN()) 1
            else if (it > 1.0) it.toInt()
            else 0
        }, {
            if (it < -1.0) abs(it) - 2
            else Float.NaN
        })
    }

    private fun SimpleModel.writeParts() {
        sb + "Parts (size: " + facesCount.size += "):"
        for ((i, count) in facesCount.withIndex()) {
            val str = "Part[$i] (size: $count, values: ${count * 3}):\n" +
                    getPartFaces(parts[i]).indentBy(indent)
            sb + str.indentBy(indent)

        }
        sb.appendln()
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
                if (rem != 0) strB.appendln()
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
        sb.appendln()
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
        sb.appendln()
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
        else max("% 1d".format(min()!!).length,
                "% 1d".format(max()!!).length)
    }

    private fun writeArray(array: FloatArray,
                           valuesPerLine: Int,
                           maxIndex: Int,
                           isRepeated: (Float) -> Int,
                           isIndexed: (Float) -> Float): String {
        val strB = StringBuilder()
        var offset = 0
        var previous = PreviousValue.COMMON
        val l = maxFloatLength(array)
        val indexedMax =
                if (maxIndex == -1) l
                else maxIndex

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
                strB += "[%${indexedMax}f]".format(indexed)
                offset += valuesPerLine - 1
                offset %= valuesPerLine
            } else {
                previous = PreviousValue.COMMON
                val isLast = (i + offset) % valuesPerLine == valuesPerLine - 1
                strB + "% ${l}f".format(value) + ',' +
                        if (isLast) '\n'
                        else ' '
            }
        }

        if (array.isNotEmpty() && previous == PreviousValue.COMMON)
            strB.removeLast(",")

        return strB.toString()
    }

    // ----- Help functions -----

    private fun StringBuilder.removeLast(subString: String) {
        val lastSubStrIndex = lastIndexOf(subString)
        delete(lastSubStrIndex, lastSubStrIndex + subString.length)
    }

    private fun maxFloatLength(floats: FloatArray): Int =
            floats.map { "% 1f".format(it).length }.max() ?: 4

}
