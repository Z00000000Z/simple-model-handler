package ru.gnylka.smh.processing.data

import org.joml.Vector3d

data class Material(
        val id: String,
        val ambient: Vector3d?,
        val diffuse: Vector3d?,
        val specular: Vector3d?,
        val opacity: Double,
        val shininess: Double,
        val diffuseTextures: List<String>,
        val specularTextures: List<String>
)
