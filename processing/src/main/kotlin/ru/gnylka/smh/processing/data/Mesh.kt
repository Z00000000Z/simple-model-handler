package ru.gnylka.smh.processing.data

data class Mesh(
        val attributes: List<MeshAttribute>,
        val vertices: FloatArray,
        val parts: List<Part>
)
