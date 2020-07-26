package ru.gnylka.smh.testjfx

import javafx.geometry.Insets
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import ru.gnylka.smh.model.data.SimpleMaterial
import ru.gnylka.smh.model.data.SimpleModel
import ru.gnylka.smh.model.data.SimpleNode
import ru.gnylka.smh.model.data.SimpleNodePart

class ModelInfo(private val model: SimpleModel) {

    val modelTreeView = TreeView<String>()
        private val modelItem = TreeItem<String>()
            private val pointsInfoItem = TreeItem<String>()
            private val normalsInfoItem = TreeItem<String>()
            private val texCoordsInfoItem = TreeItem<String>()
            private val partsItem = TreeItem<String>()
            private val materialsItem = TreeItem<String>()
            private val nodesItem = TreeItem<String>()
            private val globalPropertiesItem = TreeItem<String>()

    init {
        modelItem.children.addAll(
                pointsInfoItem,
                normalsInfoItem,
                texCoordsInfoItem,
                partsItem,
                materialsItem,
                nodesItem,
                globalPropertiesItem
        )

        modelTreeView.root = modelItem

        fillData()
    }

    private fun updateItemsData() {
        pointsInfoItem.value = "${model.pointsCount} points"
        normalsInfoItem.value = "${model.normalsCount} normals"
        texCoordsInfoItem.value = "${model.texCoordsCount} texture coordinates"
        partsItem.value = "${model.parts.size} parts"
        materialsItem.value = "${model.materials.size} materials"
        nodesItem.value = "${model.nodes.size} root nodes"
        globalPropertiesItem.value = "${model.globalProperties.size} global properties"
    }

    private fun fillData() {
        modelItem.value = "Model"
        pointsInfoItem.value = "${model.pointsCount} points " +
                "(${model.points.size} values)"
        normalsInfoItem.value = "${model.normalsCount} normals " +
                "(${model.normals.size} values)"
        texCoordsInfoItem.value = "${model.texCoordsCount} texture coordinates " +
                "(${model.texCoords.size} values)"
        partsItem.value = "${model.parts.size} parts"
        materialsItem.value = "${model.materials.size} materials"
        nodesItem.value = "${model.nodes.size} root nodes"
        globalPropertiesItem.value = "${model.globalProperties.size} global properties"

        fillParts()
        fillMaterials()
        fillNodes(nodesItem, model.nodes)
        fillProperties(globalPropertiesItem, model.globalProperties)
    }

    private fun fillParts() {
        for ((i, part) in model.parts.withIndex())
            partsItem.children += TreeItem<String>("Part[$i]: ${part.size} vertices")
    }

    private fun fillMaterials() {
        for ((i, material) in model.materials.withIndex())
            materialsItem.children += createMaterialItem(material, i)
    }

    private fun createMaterialItem(material: SimpleMaterial, index: Int): TreeItem<String> {
        val materialItem = TreeItem<String>("Material[$index]")

        val ambientItem = TreeItem<String>("Ambient", colorBox(material.ambient))
        val diffuseItem = TreeItem<String>("Diffuse", colorBox(material.diffuse))
        val specularItem = TreeItem<String>("Specular", colorBox(material.specular))

        val opacityItem = TreeItem<String>("Opacity: ${material.opacity / 255f}")
        val shininessItem = TreeItem<String>("Shininess: ${material.shininess}")

        val diffuseTextureItem = TreeItem<String>("Diffuse texture: ${
            if (material.diffuseTexture.isEmpty()) "<none>"
            else material.diffuseTexture
        }")
        val specularTextureItem = TreeItem<String>("Specular texture: ${
            if (material.specularTexture.isEmpty()) "<none>"
            else material.specularTexture
        }")

        materialItem.children.addAll(
                ambientItem,
                diffuseItem,
                specularItem,
                opacityItem,
                shininessItem,
                diffuseTextureItem,
                specularTextureItem
        )

        return materialItem
    }

    private fun colorBox(colorArray: IntArray) = Pane().apply {
        minWidth = 10.0
        minHeight = 10.0
        background = Background(BackgroundFill(
                Color.rgb(colorArray[0], colorArray[1], colorArray[2]),
                CornerRadii(0.25, true),
                Insets.EMPTY
        ))
    }

    private fun fillNodes(inItem: TreeItem<String>, fromNodes: Array<SimpleNode>) {
        for ((i, node) in fromNodes.withIndex())
            inItem.children += createNodeItem(node, i)
    }

    private fun createNodeItem(node: SimpleNode, index: Int): TreeItem<String> {
        val nodeItem = TreeItem<String>("[$index] ${node.id}")

        val translationItem = TreeItem<String>("Translation: ${joinArray(node.translation)}")
        val rotationItem = TreeItem<String>("Rotation: ${joinArray(node.rotation)}")
        val scaleItem = TreeItem<String>("Scale: ${joinArray(node.scale)}")

        val nodePartsItem = TreeItem<String>("${node.nodeParts.size} node parts")
        fillNodeParts(nodePartsItem, node.nodeParts)

        val childrenItem = TreeItem<String>("${node.children.size} children")
        fillNodes(childrenItem, node.children)

        val propertiesItem = TreeItem<String>("${node.properties.size} properties")
        fillProperties(propertiesItem, node.properties)

        nodeItem.children.addAll(
                translationItem,
                rotationItem,
                scaleItem,
                nodePartsItem,
                childrenItem,
                propertiesItem
        )

        return nodeItem
    }

    private fun joinArray(array: FloatArray) = array.joinToString(", ", "[", "]")

    private fun fillNodeParts(inItem: TreeItem<String>, fromParts: Array<SimpleNodePart>) {
        for (part in fromParts)
            inItem.children += TreeItem<String>(
                    "Part[${part.partIndex}]; Material[${part.materialIndex}]"
            )
    }

    private fun fillProperties(inItem: TreeItem<String>, properties: Array<String>) {
        for (property in properties)
            inItem.children += TreeItem<String>(property)
    }

}
