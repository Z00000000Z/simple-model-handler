package ru.gnylka.smh.processing.input

data class InputMaterial(
        val id: String?,
        val ambient: DoubleArray?,
        val diffuse: DoubleArray?,
        val emissive: DoubleArray?,
        val specular: DoubleArray?,
        val opacity: Double,
        val shininess: Double,
        val textures: Array<InputTexture?>?
)
