package ru.gnylka.smh.processing.normalization

import picocli.CommandLine
import picocli.CommandLine.ParameterException
import ru.gnylka.smh.processing.ProcessingPluginException
import ru.gnylka.smh.processing.ProcessingPluginProvider
import ru.gnylka.smh.processing.data.Model

class NormalizationPluginProvider : ProcessingPluginProvider {

    private val container = ArgumentsContainer()

    override val pluginName = "normalization"

    override fun applyArguments(args: Array<String>) {
        val cli = CommandLine(container)
        try {
            cli.parseArgs(*args)
        } catch (e: ParameterException) {
            throw ProcessingPluginException(e)
        }
    }

    /*
        fbx-conv outputs model with root nodes
            - scaled by 100 (this applies to nodes' scaling and translation)
            - rotated around X axis by PI / 2 radians
     */
    override fun modifyModel(model: Model) = container.run {
        for ((id, translation, _, scale) in model.nodes)
            if (nodeIdPattern?.matcher(id)?.matches() != false) {
                if (normalizeTranslation) translation.mul(0.01)
                if (normalizeScale) scale.mul(0.01)
            }

        model
    }

    override fun showHelp(): String? = CommandLine(container).usageMessage

}
