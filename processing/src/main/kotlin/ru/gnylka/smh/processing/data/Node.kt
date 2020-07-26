package ru.gnylka.smh.processing.data

import org.joml.Quaterniond
import org.joml.Vector3d

data class Node(
        val id: String,
        val translation: Vector3d,
        val rotation: Quaterniond,
        val scale: Vector3d,
        val nodeParts: List<NodePart>,
        val children: List<Node>,
        val properties: List<String>
)
