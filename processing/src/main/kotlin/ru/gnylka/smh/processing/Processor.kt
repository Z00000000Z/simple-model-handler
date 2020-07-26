package ru.gnylka.smh.processing

import ru.gnylka.smh.processing.data.Model
import ru.gnylka.smh.processing.input.InputModel
import ru.gnylka.smh.processing.internal.DUPLICATE_PLUGINS
import ru.gnylka.smh.processing.internal.INVALID_PLUGIN_NAME
import ru.gnylka.smh.processing.internal.Input2Data
import ru.gnylka.smh.processing.internal.ModelProcessing
import ru.gnylka.smh.utils.findDuplicatesAndThrow
import java.util.*

/**
 * Converts [InputModel] to [Model], then applies all valid plugins changes to it
 *
 * @param inputModel model to convert
 * @param propertyPrefix if node's ID begins with this string, node will be treated as a property.
 * Leave empty to skip property detection
 * @param skipInvalidProperties if node's ID begins with [propertyPrefix], but does not fit other
 * requirements, skip it
 * @param pluginPredicate a predicate, that filters plugins by their name
 * @param pluginArguments a map that contains plugin arguments
 *
 * @return model with changes applied by plugins
 *
 * @author Z00000000Z
 */
fun processModel(
        inputModel: InputModel,
        propertyPrefix: String = "",
        skipInvalidProperties: Boolean = false,
        pluginPredicate: (pluginName: String) -> Boolean,
        pluginArguments: Map<String, Array<String>> = emptyMap()
): Model {
    val dataModel = Input2Data(inputModel).createDataModel()
    return ModelProcessing(
            dataModel, propertyPrefix, skipInvalidProperties,
            pluginPredicate, pluginArguments
    ).process()
}

/**
 * Searches for available plugins using [ServiceLoader] and [ProcessingPluginProvider]
 *
 * @throws ProcessingPluginException if encountered duplicate plugins or
 * plugin with invalid (blank) name
 *
 * @see ServiceLoader
 *
 * @author Z00000000Z
 */
fun getAvailablePlugins(): Set<ProcessingPluginProvider> {
    val plugins = ServiceLoader.load(ProcessingPluginProvider::class.java)
            .filter {
                if (it.pluginName.isBlank())
                    throw ProcessingPluginException(INVALID_PLUGIN_NAME)
                else true
            }

    plugins.findDuplicatesAndThrow(ProcessingPluginProvider::pluginName) { pluginName, count ->
        throw ProcessingPluginException(DUPLICATE_PLUGINS.format(count, pluginName))
    }

    return plugins.toSet()
}
