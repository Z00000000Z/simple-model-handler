package ru.gnylka.smh.processing.input

data class InputMesh(
        val attributes: Array<String?>?,
        val vertices: FloatArray?,
        val parts: Array<InputPart?>?
)
