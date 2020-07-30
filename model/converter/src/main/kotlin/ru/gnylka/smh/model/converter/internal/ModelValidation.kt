package ru.gnylka.smh.model.converter.internal

import ru.gnylka.smh.processing.data.*
import ru.gnylka.smh.utils.findDuplicates

internal object ModelValidation {

    private const val TRIANGLE_PART_SIZE = 3

    internal fun validateModel(model: Model) {
        for (mesh in model.meshes) {
            validateMeshAttributes(mesh.attributes)
            validateMesh(mesh)
            validateMeshParts(mesh.parts)
        }

        model.materials.findDuplicates(Material::id) { element, count ->
            MULTIPLE_MATERIALS.format(element, count)
        }

        model.materials.forEach {
            it.diffuseTextures.findDuplicates { element, count ->
                MULTIPLE_DIFFUSE_TEXTURES.format(element, count)
            }
            it.specularTextures.findDuplicates { element, count ->
                MULTIPLE_SPECULAR_TEXTURES.format(element, count)
            }
        }
    }

    private fun validateMeshAttributes(attributes: List<MeshAttribute>) =
            attributes.findDuplicates(MeshAttribute::alias) { element, count ->
                MULTIPLE_MESH_ATTRIBUTES.format(element, count)
            }

    private fun validateMesh(mesh: Mesh): Unit = mesh.run {
        val valuesPerVertex = getValuesPerVertex(attributes)
        require(vertices.size % valuesPerVertex == 0) {
            ILLEGAL_MESH_VERTICES_COUNT.format(valuesPerVertex, vertices.size % valuesPerVertex)
        }
    }

    private fun validateMeshParts(parts: List<Part>) {
        for (part in parts) require(part.indices.size % TRIANGLE_PART_SIZE == 0) {
            ILLEGAL_PART_INDICES_COUNT.format(TRIANGLE_PART_SIZE,
                    part.indices.size % TRIANGLE_PART_SIZE)
        }

        parts.findDuplicates(Part::id) { element, count ->
            ILLEGAL_PARTS_COUNT.format(element, count)
        }
    }

    fun getValuesPerVertex(attributes: List<MeshAttribute>) =
            attributes.sumBy(MeshAttribute::size)

}
