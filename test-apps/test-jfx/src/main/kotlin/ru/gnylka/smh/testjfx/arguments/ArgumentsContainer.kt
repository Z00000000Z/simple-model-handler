package ru.gnylka.smh.testjfx.arguments

import javafx.scene.shape.CullFace
import picocli.CommandLine.*
import java.nio.file.Path

@Command(name = "smh-model-viewer",
        version = ["1.2"],
        description = ["A JavaFX GUI application to view .smhmb models"],
        sortOptions = false,
        showDefaultValues = true,
        showAtFileInUsageHelp = true,
        usageHelpAutoWidth = true,
        footerHeading = "\nIn-scene controls:\n",
        footer = [
            "WASD - moving",
            "Space / Shift - up / down",
            "Ctrl - speed up",
            "M - enter mouse move mode (control camera without dragging)",
            "P - pause moving",
            "R - return to world center",
            "Scroll to increase / decrease speed"
        ]
)
class ArgumentsContainer : Runnable {

    @JvmField
    @Parameters(description = ["A file with .smhmb extension to load into application"],
            paramLabel = "<file>")
    var input: Path? = null

    @JvmField
    @Option(names = ["-c", "--compress"],
            description = ["Whether to use decompression for specified input file"],
            defaultValue = "false",
            order = 0,
            negatable = true)
    var compress: Boolean = false

    @JvmField
    @Option(names = ["-t", "--textures-dir"],
            paramLabel = "<directory>",
            description = [
                "User-defined location of model textures",
                "By default, model's directory is used"
            ],
            order = 1)
    var texturesDirectory: Path? = null

    @JvmField
    @Option(names = ["-i", "--info"],
            description = ["Print additional model information in a separate window"],
            defaultValue = "false",
            order = 2,
            negatable = true)
    var info: Boolean = false

    @JvmField
    @Option(names = ["-b", "--background"],
            paramLabel = "<color>",
            description = ["Scene background web-color"],
            defaultValue = "#414A4C",
            order = 3)
    var background: String? = null

    @JvmField
    @Option(names = ["-f", "--face-culling"],
            paramLabel = "<NONE | BACK | FRONT>",
            description = [
                "Use this face culling option for all meshes in this model",
                "By default meshes' face culling won't be overridden"
            ],
            order = 4)
    var faceCulling: CullFace? = null

    @JvmField
    @Option(names = ["-l", "--light"],
            paramLabel = "<color>",
            description = [
                "Add a point light of specified web-color to the scene",
                "By default light won't be added"
            ],
            order = 5)
    var light: String? = null

    @JvmField
    @Option(names = ["-s", "--scale"],
            paramLabel = "<factor>",
            description = ["Scale model by this value"],
            defaultValue = "1.0",
            order = 6)
    var scale: Double = 1.0

    @JvmField
    @Option(names = ["-p", "--planes"],
            paramLabel = "<size>",
            description = [
                "Create XY, XZ, YZ planes with specified size",
                "Use 0.0 to exclude planes from scene"
            ],
            defaultValue = "0.0",
            order = 7)
    var planes: Double = 0.0

    @JvmField
    @Option(names = ["-a", "--acceleration"],
            description = ["Multiplier of camera's speed when accelerated"],
            defaultValue = "2.0",
            order = 8)
    var acceleration: Double = 2.0

    @JvmField
    @Option(names = ["--sensitivity"],
            description = ["Camera sensitivity"],
            defaultValue = "0.25",
            order = 9)
    var sensitivity: Double = 0.25

    @Option(names = ["-h", "--help"],
            description = ["Print help information"],
            usageHelp = true,
            order = 10)
    var help: Boolean = false

    @Option(names = ["-v", "--version"],
            description = ["Print version information"],
            versionHelp = true,
            order = 11)
    var version: Boolean = false

    override fun run() = Unit

}
