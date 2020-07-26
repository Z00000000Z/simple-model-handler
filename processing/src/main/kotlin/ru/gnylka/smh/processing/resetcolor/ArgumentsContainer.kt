package ru.gnylka.smh.processing.resetcolor

import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(name = "reset-color",
        version = ["1.0"],
        description = ["A smh-model plugin to reset material colors"],
        sortOptions = false,
        showDefaultValues = true,
        usageHelpAutoWidth = true)
class ArgumentsContainer : Runnable {

    @JvmField
    @Option(names = ["-a", "--ambient"],
            description = [
                "New ambient color values for R, G and B channels (from 0 to 1 inclusive)",
                "By default color is left untouched"
            ],
            order = 0,
            arity = "3..3")
    var ambient: DoubleArray? = null

    @JvmField
    @Option(names = ["-d", "--diffuse"],
            description = [
                "New diffuse color values for R, G and B channels (from 0 to 1 inclusive)",
                "By default color is left untouched"
            ],
            order = 1,
            arity = "3..3")
    var diffuse: DoubleArray? = null

    @JvmField
    @Option(names = ["-s", "--specular"],
            description = [
                "New specular color values for R, G and B channels (from 0 to 1 inclusive)",
                "By default color is left untouched"
            ],
            order = 2,
            arity = "3..3")
    var specular: DoubleArray? = null

    @JvmField
    @Option(names = ["-r", "--range"],
            paramLabel = "<from..to>",
            description = [
                "Indicies range of materials to change (inclusive)",
                "E.g. value of '0..2' changes color values of materials with indices 0, 1, 2"
            ],
            defaultValue = "0..*",
            order = 3)
    var range: String? = null

    override fun run() = Unit

}
