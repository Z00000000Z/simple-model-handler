package ru.gnylka.smh.testjfx

import javafx.geometry.Point3D
import javafx.scene.AmbientLight
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.CullFace
import javafx.scene.shape.MeshView
import javafx.scene.shape.TriangleMesh
import javafx.scene.shape.VertexFormat

private val planeMesh = TriangleMesh(VertexFormat.POINT_TEXCOORD).apply {
    points.addAll(
             0.5f, 0.0f,  0.5f,
            -0.5f, 0.0f,  0.5f,
            -0.5f, 0.0f, -0.5f,
             0.5f, 0.0f, -0.5f
    )
    texCoords.addAll(0.0f, 0.0f)

    faces.addAll(
            0, 0, 1, 0, 2, 0,
            0, 0, 3, 0, 2, 0
    )
}

fun createPlanes(): Group {
    val planesGroup = Group()
    val yzPlane = createPlaneMeshView(Color.ORANGERED)
    yzPlane.rotationAxis = Point3D(0.0, 0.0, 1.0)
    yzPlane.rotate = 90.0
    val xzPlane = createPlaneMeshView(Color.LIMEGREEN)
    val xyPlane = createPlaneMeshView(Color.DEEPSKYBLUE)
    xyPlane.rotationAxis = Point3D(1.0, 0.0, 0.0)
    xyPlane.rotate = 90.0

    val whiteAmbient = AmbientLight(Color.WHITE)
    whiteAmbient.scope.addAll(yzPlane, xzPlane, xyPlane)

    planesGroup.children.addAll(
            yzPlane,
            xzPlane,
            xyPlane,
            whiteAmbient
    )

    planesGroup.scaleX = planes
    planesGroup.scaleY = planes
    planesGroup.scaleZ = planes

    return planesGroup
}

private fun createPlaneMeshView(color: Color): MeshView {
    val meshView = MeshView(planeMesh)
    meshView.cullFace = CullFace.NONE
    meshView.material = PhongMaterial(color)
    return meshView
}
