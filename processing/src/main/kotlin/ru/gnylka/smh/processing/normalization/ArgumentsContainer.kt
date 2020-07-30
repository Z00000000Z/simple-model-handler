package ru.gnylka.smh.processing.normalization

import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.util.regex.Pattern

@Command(name = "normalization",
        version = ["1.0"],
        description = [
            "A smh-model plugin for normalizing models converted with fbx-conv",
            "(https://github.com/libgdx/fbx-conv)"
        ],
        sortOptions = false,
        showDefaultValues = true,
        usageHelpAutoWidth = true)
class ArgumentsContainer : Runnable {

    @JvmField
    @Option(names = ["-t", "--no-translation"],
            defaultValue = "true",
            description = ["Normalize translation of root nodes"],
            order = 0,
            negatable = true)
    var normalizeTranslation: Boolean = true

    @JvmField
    @Option(names = ["-s", "--no-scale"],
            defaultValue = "true",
            description = ["Normalize scale of root nodes"],
            order = 2,
            negatable = true)
    var normalizeScale: Boolean = true

    @JvmField
    @Option(names = ["-p", "--pattern"],
            paramLabel = "<regex>",
            description = ["Normalize node if its ID matches this pattern"],
            order = 3)
    var nodeIdPattern: Pattern? = null

    override fun run() = Unit

}
