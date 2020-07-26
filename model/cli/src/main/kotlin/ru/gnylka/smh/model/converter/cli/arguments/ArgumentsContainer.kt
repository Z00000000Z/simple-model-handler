package ru.gnylka.smh.model.converter.cli.arguments

import picocli.CommandLine.*
import ru.gnylka.smh.model.converter.cli.OptimizationOptions
import ru.gnylka.smh.model.converter.cli.OutputFileType
import java.nio.file.Path

@Command(name = "smh-model",
        version = ["1.0"],
        description = [
            "A CLI utility to convert different model files to .smhmt and .smhmb formats"
        ],
        sortOptions = false,
        showDefaultValues = true,
        showAtFileInUsageHelp = true,
        usageHelpAutoWidth = true)
class ArgumentsContainer : Runnable {

    @JvmField
    @Parameters(description = ["Files to convert or directories to search for files"],
            arity = "1..*",
            paramLabel = "<file | directory>")
    var input: Array<Path>? = null

    @JvmField
    @Option(names = ["-o", "--output"],
            paramLabel = "<directory>",
            description = ["Directory where to place result files"],
            order = 0)
    var output: Path? = null

    @JvmField
    @Option(names = ["-t", "--type"],
            paramLabel = "<text | binary>",
            description = ["Output file type"],
            order = 1)
    var fileType: OutputFileType? = null

    @JvmField
    @Option(names = ["--property-prefix"],
            paramLabel = "<prefix>",
            defaultValue = "",
            description = [
                "If node's ID begins with this string, it will be treated as property",
                "By default properties are not detected"
            ],
            showDefaultValue = Help.Visibility.NEVER,
            order = 2)
    var propertyPrefix: String? = null

    @JvmField
    @Option(names = ["--skip-invalid-properties"],
            description = ["If property is invalid, parse it as usual node"],
            defaultValue = "false",
            negatable = true,
            order = 3)
    var skipInvalidProperties: Boolean = false

    @JvmField
    @Option(names = ["-p", "--use-plugins"],
            paramLabel = "<plugin-name>",
            description = ["A comma-separated list of plugins to use"],
            split = ",",
            arity = "0..*",
            order = 4)
    var usePlugins: Array<String>? = null

    @JvmField
    @Option(names = ["--plugin"],
            paramLabel = "<pair>",
            description = [
                "Provide a comma-separated list of arguments " +
                        "for plugin using <plugin-name>=<argument>"
            ],
            order = 5)
    var pluginArguments: Map<String, String>? = null

    @JvmField
    @Option(names = ["--pick-first-texture"],
            description = ["If multiple textures are presented, the first will be chosen"],
            defaultValue = "false",
            negatable = true,
            order = 6)
    var pickFirstTexture: Boolean = false

    @JvmField
    @Option(names = ["--skip-normals"],
            description = ["Do not write normals"],
            defaultValue = "false",
            negatable = true,
            order = 7)
    var skipNormals: Boolean = false

    @JvmField
    @Option(names = ["--optimize"],
            paramLabel = "<option>",
            description = [
                "Optimization methods to apply to the model",
                "Available options: " +
                        "OPTIMIZE_POINTS, INDEX_POINTS, " +
                        "OPTIMIZE_NORMALS, INDEX_NORMALS, " +
                        "OPTIMIZE_TEX_COORDS, INDEX_TEX_COORDS, " +
                        "OPTIMIZE_PARTS",
                "Specify no options to apply all of them",
                "By default none are applied"
            ],
            split = ",",
            arity = "0..*",
            order = 8)
    var optimize: Array<OptimizationOptions>? = null

    @JvmField
    @Option(names = ["-c", "--compress"],
            description = ["Use gzip compression"],
            defaultValue = "false",
            negatable = true,
            order = 9)
    var compress: Boolean = false

    @JvmField
    @Option(names = ["--parallel"],
            description = ["Convert models using multiple threads"],
            defaultValue = "false",
            order = 10)
    var parallel: Boolean = false

    @JvmField
    @Option(names = ["--list-loaders"],
            description = ["List all available loaders"],
            defaultValue = "false",
            help = true,
            order = 11)
    var listLoaders: Boolean = false

    @JvmField
    @Option(names = ["--list-plugins"],
            description = ["List all available plugins"],
            defaultValue = "false",
            help = true,
            order = 12)
    var listPlugins: Boolean = false

    @JvmField
    @Option(names = ["-v", "--verbose"],
            description = [
                "Amount of information being logged",
                "Specify 2 times to see stacktraces on exceptions"
            ],
            order = 13)
    var verbose: BooleanArray? = null

    @Option(names = ["-h", "--help"],
            description = ["Print help information"],
            usageHelp = true,
            order = 14)
    var help: Boolean = false

    @Option(names = ["--version"],
            description = ["Print version information"],
            versionHelp = true,
            order = 15)
    var version: Boolean = false

    override fun run() = Unit

}
