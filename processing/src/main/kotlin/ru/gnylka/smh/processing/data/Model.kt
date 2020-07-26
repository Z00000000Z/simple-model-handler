package ru.gnylka.smh.processing.data

data class Model(
        val meshes: List<Mesh>,
        val materials: List<Material>,
        val nodes: List<Node>,
        val globalProperties: List<String>
)
