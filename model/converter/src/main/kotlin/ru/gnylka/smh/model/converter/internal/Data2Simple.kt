package ru.gnylka.smh.model.converter.internal

import org.joml.Quaterniond
import org.joml.Vector3d
import ru.gnylka.smh.model.converter.internal.ModelValidation.getValuesPerVertex
import ru.gnylka.smh.model.converter.internal.ModelValidation.validateModel
import ru.gnylka.smh.model.data.SimpleMaterial
import ru.gnylka.smh.model.data.SimpleModel
import ru.gnylka.smh.model.data.SimpleNode
import ru.gnylka.smh.model.data.SimpleNodePart
import ru.gnylka.smh.processing.data.*
import ru.gnylka.smh.processing.data.MeshAttribute.*
import java.util.*
import kotlin.math.round

internal class Data2Simple internal constructor(
        private val model: Model,
        private val pickFirstTexture: Boolean
) {

    private var indicesOffset = 0

    private val points = mutableListOf<Float>()
    private val normals = mutableListOf<Float>()
    private val textureCoords = mutableListOf<Float>()

    private val faceIDs = mutableListOf<String>()
    private val faces = mutableListOf<ShortArray>()

    private val materialIDs = mutableListOf<String>()
    private val materials = mutableListOf<SimpleMaterial>()

    private val nodes = mutableListOf<SimpleNode>()

    init {
        validateModel(model)
        convertModel()
    }

    internal fun createSimpleModel() = SimpleModel(
            points.toFloatArray(),
            normals.toFloatArray(),
            textureCoords.toFloatArray(),
            faces.toTypedArray(),
            materials.toTypedArray(),
            nodes.toTypedArray(),
            model.globalProperties.toTypedArray()
    )

    private fun convertModel() {
        val sortedMeshes = sortMeshes(model.meshes)
        for ((attributes, vertices, parts) in sortedMeshes) {
            addPartsIndicesOffset(parts)
            mergeVertices(vertices, attributes)
            readParts(parts)
        }

        readMaterials(model.materials)

        nodes += model.nodes.map(::convertNode)
    }

    /*
        Returns meshes only with attribute sets [POSITION, NORMAL] or [POSITION, NORMAL, TEXCOORD]
        Meshes with TEXCOORD attribute are always first
     */
    private fun sortMeshes(meshes: List<Mesh>): List<Mesh> {
        val posNorAttrs = listOf(POSITION, NORMAL)
        val posNorTexAttrs = MeshAttribute.values().toList()

        val posNorMeshes = meshes.filter {
            it.attributes.containsAll(posNorAttrs)
        }.toMutableList()

        val posNorTexMeshes = posNorMeshes.filter {
            TEXCOORD in it.attributes
        }
        posNorMeshes -= posNorTexMeshes

        require(posNorMeshes.size <= 1 || posNorTexMeshes.size <= 1) {
            ILLEGAL_MESH_ATTRIBUTES.format(posNorAttrs, posNorTexAttrs)
        }

        return posNorTexMeshes + posNorMeshes
    }

    private fun addPartsIndicesOffset(parts: List<Part>) = parts.forEach {
        for ((i, value) in it.indices.withIndex())
            it.indices[i] = value + indicesOffset
    }

    private fun mergeVertices(vertices: FloatArray, attributes: List<MeshAttribute>) {
        val valuesPerVertex = getValuesPerVertex(attributes)

        val pointsArray = getValuesForAttribute(POSITION, attributes, vertices, valuesPerVertex)
        points.addAll(pointsArray)
        indicesOffset += pointsArray.size

        val normalsArray = getValuesForAttribute(NORMAL, attributes, vertices, valuesPerVertex)
        normals.addAll(normalsArray)

        if (TEXCOORD in attributes) {
            val texArray = getValuesForAttribute(TEXCOORD, attributes, vertices, valuesPerVertex)
            textureCoords.addAll(texArray)
        }
    }

    private fun getValuesForAttribute(
            attribute: MeshAttribute,
            attributes: List<MeshAttribute>,
            vertices: FloatArray,
            valuesPerVertex: Int
    ): List<Float> {
        if (attribute !in attributes) throw NoSuchElementException(
                ATTRIBUTE_NOT_FOUND.format(attribute, attributes.joinToString(", "))
        )

        var valuesOffset = 0
        for (attr in attributes)
            if (attr == attribute) break
            else valuesOffset += attr.size

        val size = attribute.size
        val valuesSize = vertices.size / valuesPerVertex * size
        return List(valuesSize) {
            val index = it / size * valuesPerVertex + it % size + valuesOffset
            vertices[index]
        }
    }

    private fun readParts(parts: List<Part>): Unit = parts.forEach {
        (id, indices) ->
        faceIDs.add(id)
        val indicesArray = ShortArray(indices.size) {
            indices[it].toShort()
        }
        faces.add(indicesArray)
    }

    private fun readMaterials(materials: List<Material>) = materials.forEach {
        (id, ambient, diffuse, specular, opacity, shininess,
                diffuseTextures, specularTextures) ->
        materialIDs.add(id)
        val simpleMaterial = SimpleMaterial(
                colorToArray(ambient ?: Vector3d()),
                colorToArray(diffuse ?: Vector3d()),
                colorToArray(specular ?: Vector3d()),
                toIntColorChannel(opacity),
                shininess.toFloat(),
                getTexture(diffuseTextures).orEmpty(),
                getTexture(specularTextures).orEmpty()
        )
        this.materials.add(simpleMaterial)
    }

    /*
        Returns color representation in 3-elemented int array (values range from 0 to 255)
     */
    private fun colorToArray(color: Vector3d): IntArray =
            IntArray(3) { toIntColorChannel(color[it]) }

    private fun toIntColorChannel(colorChannel: Double): Int =
            round(colorChannel * 255).toInt()

    /*
        If pickFirstTexture - returns first texture or empty string
        Else - throws exception if multiple elements or empty string
     */
    private fun getTexture(textures: List<String>): String? = textures.run {
        if (size == 1) textures.first()
        else if (size > 1) {
            if (pickFirstTexture) textures.first()
            else throw IllegalArgumentException(MULTIPLE_TEXTURES.format(size))
        } else null
    }

    private fun convertNode(node: Node): SimpleNode = node.run {
        val simpleNodeParts = nodeParts.map(::convertNodePart)
        val simpleChildren = children.map(::convertNode)

        SimpleNode(
                id,
                convertVector(translation),
                convertQuaternion(rotation),
                convertVector(scale),
                simpleNodeParts.toTypedArray(),
                simpleChildren.toTypedArray(),
                properties.toTypedArray()
        )
    }

    private fun convertNodePart(nodePart: NodePart) = SimpleNodePart(
            indexOfOrThrow(faceIDs, nodePart.partID),
            indexOfOrThrow(materialIDs, nodePart.materialID)
    )

    private fun <T> indexOfOrThrow(container: List<T>, element: T): Int {
        val index = container.indexOf(element)
        if (index == -1) throw NoSuchElementException(ELEMENT_NOT_FOUND.format(element))
        else return index
    }

    private fun convertVector(translation: Vector3d) = translation.run {
        floatArrayOf(x.toFloat(), y.toFloat(), z.toFloat())
    }

    private fun convertQuaternion(rotation: Quaterniond) = rotation.run {
        floatArrayOf(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
    }

}
