package ru.gnylka.smh.processing.input

data class InputNode(
        val id: String?,
        val translation: DoubleArray?,
        val rotation: DoubleArray?,
        val scale: DoubleArray?,
        val nodeParts: Array<InputNodePart?>?,
        val children: Array<InputNode?>?
)
