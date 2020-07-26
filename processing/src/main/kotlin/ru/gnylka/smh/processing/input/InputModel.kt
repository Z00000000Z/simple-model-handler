package ru.gnylka.smh.processing.input

data class InputModel(
        val id: String?,
        val meshes: Array<InputMesh?>?,
        val materials: Array<InputMaterial?>?,
        val nodes: Array<InputNode?>?
)
