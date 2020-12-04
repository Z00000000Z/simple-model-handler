package ru.gnylka.smh.model.converter.internal

import ru.gnylka.smh.model.data.*
import ru.gnylka.smh.processing.data.MeshAttribute.NORMAL
import ru.gnylka.smh.processing.data.MeshAttribute.TEXCOORD
import java.io.DataOutputStream
import java.io.OutputStream

internal class BinaryWriting internal constructor(
        private val simpleModel: SimpleModel
) {

    internal fun writeModelAsBinary(output: OutputStream): Unit = simpleModel.run {
        val dataOutput = DataOutputStream(output)

        writeGlobalProperties(dataOutput)
        writePointsKeys(dataOutput)
        writePointsIndices(dataOutput)
        writeNormalsIndices(dataOutput)
        writeTexCoordsIndices(dataOutput)

        points.writeArrayWithSize(pointsCount.toShort(), dataOutput)
        normals.writeArrayWithSize(normalsCount.toShort(), dataOutput)
        texCoords.writeArrayWithSize(texCoordsCount.toShort(), dataOutput)

        writeParts(dataOutput)
        writeMaterials(dataOutput)
        writeNodes(dataOutput)
    }

    private fun SimpleModel.writeGlobalProperties(output: DataOutputStream) {
        output.writeByte(globalProperties.size)
        globalProperties.forEach(output::writeUTF)
    }

    private fun SimpleModel.writePointsKeys(output: DataOutputStream) {
        pointsKeys.writeArrayWithSize(pointsKeys.size.toShort(), output)
    }

    private fun SimpleModel.writePointsIndices(output: DataOutputStream) {
        pointsIndices.writeArray(output)
    }

    private fun SimpleModel.writeNormalsIndices(output: DataOutputStream) {
        val size = normalsIndices.size / NORMAL.size
        normalsIndices.writeArrayWithSize(size.toShort(), output)
    }

    private fun SimpleModel.writeTexCoordsIndices(output: DataOutputStream) {
        val size = texCoordsIndices.size / TEXCOORD.size
        texCoordsIndices.writeArrayWithSize(size.toShort(), output)
    }

    private fun SimpleModel.writeParts(output: DataOutputStream) {
        output.writeByte(facesCount.size)
        for (i in facesCount.indices) {
            output.writeShort(facesCount[i])
            parts[i].writeArray(output)
        }
    }

    private fun SimpleModel.writeMaterials(output: DataOutputStream) {
        output.writeByte(materials.size)
        materials.forEach { it.writeMaterial(output) }
    }

    private fun SimpleMaterial.writeMaterial(output: DataOutputStream) {
        ambient.writeColor(output)
        diffuse.writeColor(output)
        specular.writeColor(output)
        output.writeByte(opacity)
        output.writeFloat(shininess)
        output.writeUTF(diffuseTexture)
        output.writeUTF(specularTexture)
    }

    private fun SimpleModel.writeNodes(output: DataOutputStream) {
        output.writeByte(nodes.size)
        nodes.forEach { it.writeNode(output) }
    }

    private fun SimpleNode.writeNode(output: DataOutputStream) {
        output.writeUTF(id)
        translation.writeArray(output)
        rotation.writeArray(output)
        scale.writeArray(output)

        output.writeByte(properties.size)
        properties.forEach(output::writeUTF)

        output.writeByte(nodeParts.size)
        nodeParts.forEach {
            output.writeByte(it.partIndex)
            output.writeByte(it.materialIndex)
        }

        output.writeByte(children.size)
        children.forEach { it.writeNode(output) }
    }

    private fun ShortArray.writeArray(output: DataOutputStream) = forEach {
        output.writeShort(it.toInt())
    }

    private fun IntArray.writeArray(output: DataOutputStream) = forEach(output::writeInt)

    private fun FloatArray.writeArrayWithSize(size: Short, output: DataOutputStream) {
        output.writeShort(size.toInt())
        writeArray(output)
    }

    private fun FloatArray.writeArray(output: DataOutputStream) = forEach(output::writeFloat)

    private fun IntArray.writeColor(output: DataOutputStream) = forEach(output::writeByte)

}
