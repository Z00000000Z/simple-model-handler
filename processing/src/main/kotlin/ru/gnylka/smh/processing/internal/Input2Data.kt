package ru.gnylka.smh.processing.internal

import org.joml.Quaterniond
import org.joml.Vector3d
import ru.gnylka.smh.processing.data.*
import ru.gnylka.smh.processing.input.*

internal class Input2Data internal constructor(
        private val inputModel: InputModel
) {

    companion object {

        private const val REQUIRED_PART_TYPE = "TRIANGLES"
        private const val DIFFUSE_TEXTURE_TYPE_ALIAS = "DIFFUSE"
        private const val SPECULAR_TEXTURE_TYPE_ALIAS = "SPECULAR"
        private const val AMBIENT_COLOR_ALIAS = "Ambient"
        private const val DIFFUSE_COLOR_ALIAS = "Diffuse"
        private const val SPECULAR_COLOR_ALIAS = "Specular"

    }

    private val meshes: List<Mesh>
    private val materials: List<Material>
    private val nodes: List<Node>

    init {
        meshes = convertMeshes(inputModel.meshes)
        materials = convertMaterials(inputModel.materials)
        nodes = convertNodes(inputModel.nodes)
    }

    internal fun createDataModel() = Model(meshes, materials, nodes, emptyList())

    private fun convertMeshes(meshes: Array<InputMesh?>?) =
            meshes?.map(::convertMesh) ?: emptyList()

    private fun convertMesh(mesh: InputMesh?) = mesh?.run {
        val attributes = convertAttributes(attributes)
        val vertices = vertices ?: throwWith(VERTICES_NOT_SPECIFIED)
        val parts = convertParts(parts)
        Mesh(attributes, vertices, parts)
    } ?: throwWith(MESH_NOT_SPECIFIED)

    private fun convertAttributes(attributes: Array<String?>?) =
            if (attributes.isNullOrEmpty()) throwWith(ATTRIBUTES_NOT_SPECIFIED)
            else attributes.map(::convertAttribute)

    private fun convertAttribute(attribute: String?) = attribute?.run {
        MeshAttribute.values()
                .firstOrNull { it.alias == attribute }
                ?: throwWith(UNKNOWN_ATTRIBUTE_EXCEPTION, attribute)
    } ?: throwWith(ATTRIBUTE_NOT_SPECIFIED)

    private fun convertParts(parts: Array<InputPart?>?) =
            if (parts.isNullOrEmpty()) throwWith(PARTS_NOT_SPECIFIED)
            else parts.map(::convertPart)

    private fun convertPart(part: InputPart?) = part?.run {
        val id = id ?: throwWith(PART_ID_NOT_SPECIFIED)
        if (type != REQUIRED_PART_TYPE) throwWith(UNKNOWN_PART_TYPE, type)
        val indices = indices ?: throwWith(INDICES_NOT_SPECIFIED)
        Part(id, indices)
    } ?: throwWith(PART_NOT_SPECIFIED)

    private fun convertMaterials(materials: Array<InputMaterial?>?) =
            materials?.map(::convertMaterial) ?: emptyList()

    private fun convertMaterial(material: InputMaterial?) = material?.run {
        val id = id ?: throwWith(MATERIAL_ID_NOT_SPECIFIED)
        val ambient = convertColor(ambient, AMBIENT_COLOR_ALIAS)
        val diffuse = convertColor(diffuse, DIFFUSE_COLOR_ALIAS)
        if (!isInNormalBound(opacity)) throwWith(OPACITY_VALUE_OUT_OF_BOUND, opacity)
        val specular = convertColor(specular, SPECULAR_COLOR_ALIAS)
        val checkedTextures = validateTextures(textures)
        val diffuseTextures = getTexturesOfType(DIFFUSE_TEXTURE_TYPE_ALIAS, checkedTextures)
        val specularTextures = getTexturesOfType(SPECULAR_TEXTURE_TYPE_ALIAS, checkedTextures)
        Material(id, ambient, diffuse, specular, opacity,
                shininess, diffuseTextures, specularTextures)
    } ?: throwWith(MATERIAL_NOT_SPECIFIED)

    private fun convertColor(color: DoubleArray?, colorName: String) = color?.run {
        if (size != 3) throwWith(INCORRECT_COLOR_VALUES_COUNT, size)

        val red = get(0)
        val green = get(1)
        val blue = get(2)
        if (!isInNormalBound(red)) throwWith(RED_VALUE_OUT_OF_BOUND, red)
        if (!isInNormalBound(green)) throwWith(GREEN_VALUE_OUT_OF_BOUND, green)
        if (!isInNormalBound(blue)) throwWith(BLUE_VALUE_OUT_OF_BOUND, blue)
        Vector3d(red, green, blue)
    } ?: throwWith(COLOR_NOT_SPECIFIED, colorName)

    private fun validateTextures(textures: Array<InputTexture?>?): Array<InputTexture> =
            textures?.map { it ?: throwWith(TEXTURE_NOT_SPECIFIED) }
                    ?.onEach {
                        it.fileName ?: throwWith(TEXTURE_FILE_NOT_SPECIFIED)
                        val type = it.type ?: throwWith(TEXTURE_TYPE_NOT_SPECIFIED)
                        if (type != DIFFUSE_TEXTURE_TYPE_ALIAS &&
                                type != SPECULAR_TEXTURE_TYPE_ALIAS)
                            throwWith(UNKNOWN_TEXTURE_TYPE, type)
                    }?.toTypedArray() ?: emptyArray()

    private fun getTexturesOfType(textureType: String,
                                  textures: Array<InputTexture>) =
            textures.filter { it.type!! == textureType }
                    .map(InputTexture::fileName)
                    .requireNoNulls()

    private fun convertNodes(nodes: Array<InputNode?>?): List<Node> =
            nodes?.map(::convertNode) ?: emptyList()

    private fun convertNode(node: InputNode?): Node = node?.run {
        val id = id ?: throwWith(NODE_ID_NOT_SPECIFIED)
        val translation = convertTranslation(translation)
        val rotation = convertRotation(rotation)
        val scale = convertScale(scale)
        val nodeParts = convertNodeParts(nodeParts)
        val children = convertChildren(children)
        return Node(id, translation, rotation, scale, nodeParts, children, emptyList())
    } ?: throwWith(NODE_NOT_SPECIFIED)

    private fun convertTranslation(translation: DoubleArray?) = translation?.run {
        if (size != 3) throwWith(INCORRECT_TRANSLATION_VALUES_COUNT, size)
        Vector3d(get(0), get(1), get(2))
    } ?: Vector3d()

    private fun convertRotation(rotation: DoubleArray?) = rotation?.run {
        if (size != 4) throwWith(INCORRECT_ROTATION_VALUES_COUNT, size)
        else Quaterniond(get(0), get(1), get(2), get(3))
    } ?: Quaterniond()

    private fun convertScale(scale: DoubleArray?) = scale?.run {
        if (size != 3) throwWith(INCORRECT_SCALE_VALUES_COUNT, size)
        Vector3d(get(0), get(1), get(2))
    } ?: Vector3d(1.0)

    private fun convertNodeParts(parts: Array<InputNodePart?>?) =
            parts?.map(::convertNodePart) ?: emptyList()

    private fun convertNodePart(part: InputNodePart?) = part?.run {
        val partID = meshPartID ?: throwWith(NODE_PART_ID_NOT_SPECIFIED)
        val materialID = materialID ?: throwWith(NODE_PART_MATERIAL_ID_NOT_SPECIFIED)
        NodePart(partID, materialID)
    } ?: throwWith(NODE_PART_NOT_SPECIFIED)

    private fun convertChildren(children: Array<InputNode?>?) =
            children?.map(::convertNode) ?: emptyList()

    // ----- Help functions -----

    private fun isInNormalBound(value: Double) = value in 0.0..1.0

}
