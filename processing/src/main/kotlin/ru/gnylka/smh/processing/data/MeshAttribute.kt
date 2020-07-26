package ru.gnylka.smh.processing.data

enum class MeshAttribute(
        val alias: String,
        val size: Int
) {

    POSITION("POSITION", 3),
    NORMAL("NORMAL", 3),
    TEXCOORD("TEXCOORD0", 2)

}