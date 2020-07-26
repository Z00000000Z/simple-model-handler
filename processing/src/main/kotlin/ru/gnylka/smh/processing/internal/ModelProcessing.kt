package ru.gnylka.smh.processing.internal

import ru.gnylka.smh.processing.IllegalPropertyException
import ru.gnylka.smh.processing.ProcessingPluginException
import ru.gnylka.smh.processing.data.Model
import ru.gnylka.smh.processing.data.Node
import ru.gnylka.smh.processing.getAvailablePlugins

internal class ModelProcessing internal constructor(
        private var model: Model,
        private val propertyPrefix: String,
        private val skipInvalidProperties: Boolean,
        private val pluginPredicate: (String) -> Boolean,
        private val pluginArguments: Map<String, Array<String>>
) {

    internal fun process(): Model {
        if (propertyPrefix.isNotEmpty()) parseAllProperties()
        processPlugins()
        return model
    }

    private fun parseAllProperties() {
        var (rootNodes, globalProperties) = parsePropertiesFrom(model.nodes)
        rootNodes = rootNodes.map(::parsePropertiesRecursive)
        model = model.copy(nodes = rootNodes, globalProperties = globalProperties)
    }

    private fun parsePropertiesRecursive(node: Node): Node = node.run {
        var (updatedChildren, properties) = parsePropertiesFrom(node.children)
        updatedChildren = updatedChildren.map(::parsePropertiesRecursive)
        copy(children = updatedChildren, properties = properties)
    }

    /*
        Returns Pair<MutableList<Node>, List<String>> where
            MutableList<Node> are non-properties nodes
            List<String> are valid properties
     */
    private fun parsePropertiesFrom(nodes: List<Node>): Pair<
            List<Node>,
            List<String>> {
        val updatedNodes = mutableListOf<Node>()
        val properties = mutableListOf<String>()

        for (node in nodes)
            convertNodeIfValidOrNull(node)
                    ?.let(properties::add)
                    ?: updatedNodes.add(node)

        return updatedNodes to properties
    }

    private fun convertNodeIfValidOrNull(node: Node): String? =
            if (node.id.startsWith(propertyPrefix))
                if (!isValidPropertyNode(node)) {
                    if (skipInvalidProperties) null
                    else validatePropertyNode(node)
                } else node.id.substringAfter(propertyPrefix)
            else null

    private fun isValidPropertyNode(node: Node): Boolean = node.run {
        nodeParts.isEmpty() && children.isEmpty()
    }

    private fun validatePropertyNode(node: Node): Nothing = node.run {
        val partsCount = nodeParts.size
        if (partsCount > 0) throw IllegalPropertyException(
                NODE_PARTS_IN_PROPERTY.format(id, partsCount))

        val childrenCount = children.size
        if (childrenCount > 0) throw IllegalPropertyException(
                CHILDREN_IN_PROPERTY.format(id, childrenCount))

        error("This cannot happen")
    }

    private fun processPlugins() {
        val validPlugins = getAvailablePlugins().filter {
            pluginPredicate(it.pluginName)
        }

        for (plugin in validPlugins) {
            val pluginName = plugin.pluginName
            val arguments = pluginArguments[pluginName] ?: emptyArray()

            try {
                plugin.applyArguments(arguments)

                model = requireNotNull(plugin.modifyModel(model)) {
                    INCORRECT_PLUGIN_RESULT.format(pluginName)
                }
            } catch (e: ProcessingPluginException) {
                e.pluginName = pluginName
                throw e
            }
        }
    }

}
