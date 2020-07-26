package ru.gnylka.smh.loader.g3dj

import ru.gnylka.smh.processing.input.*

internal class G3dj2Input internal constructor(
        private val g3djModel: G3djModel
) {

    private val meshes: Array<InputMesh?>?
    private val materials: Array<InputMaterial?>?
    private val nodes: Array<InputNode?>?

    init {
        meshes = convertMeshes(g3djModel.meshes)
        materials = convertMaterials(g3djModel.materials)
        nodes = convertNodes(g3djModel.nodes)
    }

    internal fun createInputModel() = InputModel(
            g3djModel.id,
            meshes,
            materials,
            nodes
    )

    private fun convertMeshes(meshes: Array<G3djMesh?>?): Array<InputMesh?>? =
        meshes?.map(::convertMesh)?.toTypedArray()

    private fun convertMesh(mesh: G3djMesh?): InputMesh? = mesh?.run {
        InputMesh(attributes, vertices, parts?.map(::convertPart)?.toTypedArray())
    }

    private fun convertPart(part: G3djPart?): InputPart? = part?.run {
        InputPart(id, type, indices)
    }

    private fun convertMaterials(materials: Array<G3djMaterial?>?): Array<InputMaterial?>? =
            materials?.map(::convertMaterial)?.toTypedArray()

    private fun convertMaterial(material: G3djMaterial?): InputMaterial? = material?.run {
        InputMaterial(id, ambient, diffuse, emissive, specular,
                opacity, shininess, convertTextures(textures))
    }

    private fun convertTextures(textures: Array<G3djTexture?>?): Array<InputTexture?>? =
            textures?.map(::convertTexture)?.toTypedArray()

    private fun convertTexture(texture: G3djTexture?): InputTexture? = texture?.run {
        InputTexture(filename, type)
    }

    private fun convertNodes(nodes: Array<G3djNode?>?): Array<InputNode?>? =
            nodes?.map(::convertNode)?.toTypedArray()

    private fun convertNode(node: G3djNode?): InputNode? = node?.run {
        InputNode(id, translation, rotation, scale,
                convertNodeParts(parts),
                convertNodes(children))
    }

    private fun convertNodeParts(nodeParts: Array<G3djNodePart?>?): Array<InputNodePart?>? =
            nodeParts?.map(::convertNodePart)?.toTypedArray()

    private fun convertNodePart(nodePart: G3djNodePart?): InputNodePart? = nodePart?.run {
        InputNodePart(meshpartid, materialid)
    }

}
