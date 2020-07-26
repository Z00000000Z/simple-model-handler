package ru.gnylka.smh.processing.resetcolor

import picocli.CommandLine
import picocli.CommandLine.ParameterException
import ru.gnylka.smh.processing.ProcessingPluginException
import ru.gnylka.smh.processing.ProcessingPluginProvider
import ru.gnylka.smh.processing.data.Material
import ru.gnylka.smh.processing.data.Model

class ResetColorPluginProvider : ProcessingPluginProvider {

    private val container = ArgumentsContainer()

    private lateinit var indicesRange: IntRange

    override val pluginName = "reset-color"

    override fun applyArguments(args: Array<String>) {
        val cli = CommandLine(container)
        try {
            cli.parseArgs(*args)
        } catch (e: ParameterException) {
            throw ProcessingPluginException(e)
        }

        validateArgs()
        parseRange()
    }

    override fun modifyModel(model: Model) = model.apply {
        for ((i, material) in materials.withIndex())
            if (i in indicesRange) modifyMaterial(material)
    }

    override fun showHelp(): String? = CommandLine(container).usageMessage

    private fun validateArgs() {
        val rangeMatches = container.range!!.matches("\\d+\\.\\.(\\d+|\\*)".toRegex())
        if (!rangeMatches) throw ProcessingPluginException("Specified range is incorrent")

        container.ambient?.let {
            validateColor(it, "Ambient color contains out-of-bounds channel value")
        }
        container.diffuse?.let {
            validateColor(it, "Diffuse color contains out-of-bounds channel value")
        }
        container.specular?.let {
            validateColor(it, "Specular color contains out-of-bounds channel value")
        }
    }

    private fun validateColor(colorArray: DoubleArray, message: String) {
        colorArray.find { it !in 0.0..1.0 }?.let {
            throw ProcessingPluginException(message)
        }
    }

    private fun parseRange() {
        val range = container.range!!

        val from = range.substringBefore("..").toInt()

        val toStr = range.substringAfter("..")
        val to =
                if (toStr == "*") Int.MAX_VALUE
                else toStr.toInt()

        indicesRange = from..to
        if (indicesRange.isEmpty()) throw ProcessingPluginException(
                "Illegal indices range $indicesRange")
    }

    private fun modifyMaterial(material: Material) {
        container.ambient?.let {
            material.ambient?.set(it)
        }

        container.diffuse?.let {
            material.diffuse?.set(it)
        }

        container.specular?.let {
            material.specular?.set(it)
        }
    }

}
