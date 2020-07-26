package ru.gnylka.smh.processing

import ru.gnylka.smh.processing.data.Model

/**
 * Interface that must be implemented by plugins
 */
interface ProcessingPluginProvider {

    /**
     * A stateless, unique plugin name
     */
    val pluginName: String

    /**
     * A function used to pass arguments to this plugin
     *
     * @param args arguments specified for this plugin
     *
     * @throws ProcessingPluginException if e.g. specified arguments are incorrect
     */
    @Throws(ProcessingPluginException::class)
    fun applyArguments(args: Array<String>) = Unit

    /**
     * A function used to pass model to this plugin for further modifications
     *
     * @param model a model to modify
     *
     * @throws ProcessingPluginException if any exception occured while modifying model
     *
     * @return a new model with modifications applied
     */
    @Throws(ProcessingPluginException::class)
    fun modifyModel(model: Model): Model

    /**
     * A function used to get plugin's help string
     *
     * @return plugin's help string or null if plugin does not handle arguments
     */
    fun showHelp(): String? = null

}
