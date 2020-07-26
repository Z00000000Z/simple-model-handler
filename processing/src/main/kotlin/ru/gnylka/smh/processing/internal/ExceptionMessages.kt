package ru.gnylka.smh.processing.internal

import ru.gnylka.smh.processing.ProcessingException

const val VERTICES_NOT_SPECIFIED = "Vertices not specified"
const val MESH_NOT_SPECIFIED = "Mesh is not specified"
const val ATTRIBUTES_NOT_SPECIFIED = "Attributes are not specified"
const val UNKNOWN_ATTRIBUTE_EXCEPTION = "Unknown attribute: %s"
const val ATTRIBUTE_NOT_SPECIFIED = "Attribute is not specified"
const val PARTS_NOT_SPECIFIED = "Parts are not specified"
const val PART_ID_NOT_SPECIFIED = "Part ID is not specified"
const val UNKNOWN_PART_TYPE = "Unknown part type: %s"
const val INDICES_NOT_SPECIFIED = "Indices are not specified"
const val PART_NOT_SPECIFIED = "Part is not specified"
const val MATERIAL_ID_NOT_SPECIFIED = "Material ID is not specified"
const val OPACITY_VALUE_OUT_OF_BOUND = "Expected opacity value in [0.0; 1.0] (got %s)"
const val MATERIAL_NOT_SPECIFIED = "Material is not specified"
const val INCORRECT_COLOR_VALUES_COUNT = "Expected 3 color values (got %s)"
const val RED_VALUE_OUT_OF_BOUND = "Expected red value in [0.0; 1.0] (got %s)"
const val GREEN_VALUE_OUT_OF_BOUND = "Expected green value in [0.0; 1.0] (got %s)"
const val BLUE_VALUE_OUT_OF_BOUND = "Expected blue value in [0.0; 1.0] (got %s)"
const val COLOR_NOT_SPECIFIED = "%s color is not specified"
const val TEXTURE_NOT_SPECIFIED = "Texture is not specified"
const val TEXTURE_FILE_NOT_SPECIFIED = "Texture file is not specified"
const val TEXTURE_TYPE_NOT_SPECIFIED = "Texture type is not specified"
const val UNKNOWN_TEXTURE_TYPE = "Unknown texture type: %s"
const val NODE_ID_NOT_SPECIFIED = "Node ID is not specified"
const val NODE_NOT_SPECIFIED = "Node is not specified"
const val INCORRECT_TRANSLATION_VALUES_COUNT = "Expected 3 translation values (got %s)"
const val INCORRECT_ROTATION_VALUES_COUNT = "Expected 4 rotation values (got %s)"
const val INCORRECT_SCALE_VALUES_COUNT = "Expected 3 scale values (got %s)"
const val NODE_PART_ID_NOT_SPECIFIED = "Node part ID is not specified"
const val NODE_PART_MATERIAL_ID_NOT_SPECIFIED = "Node part material ID is not specified"
const val NODE_PART_NOT_SPECIFIED = "Node part is not specified"

const val NODE_PARTS_IN_PROPERTY = "Property node with ID %s contains %s parts (must be 0)"
const val CHILDREN_IN_PROPERTY = "Property node with ID %s contains %s children (must be 0)"
const val INVALID_PLUGIN_NAME = "Invalid plugin name"
const val DUPLICATE_PLUGINS = "Plugins must have unique names (found %s plugins named %s)"
const val INCORRECT_PLUGIN_RESULT = "Plugin %s returned null instead of model"

fun throwWith(message: String?, vararg formatValues: Any?): Nothing =
        throw ProcessingException(message?.format(formatValues))
