package ru.gnylka.smh.testjfx

import javafx.application.Application
import javafx.scene.*
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.CullFace
import javafx.scene.shape.Shape3D
import javafx.stage.Stage
import ru.gnylka.smh.model.data.SimpleModel
import ru.gnylka.smh.model.fxhandler.FXHandler
import ru.gnylka.smh.model.loader.ModelLoader
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.util.logging.LogManager.getLogManager
import java.util.logging.Logger
import java.util.zip.GZIPInputStream

class ModelViewer : Application() {

    private lateinit var camCtrl: FPSCameraController

    private val logger: Logger

    private lateinit var primaryStage: Stage

    init {
        val logConfig = ModelViewer::class.java.getResourceAsStream("/logging.properties")
        getLogManager().readConfiguration(logConfig)
        logger = Logger.getLogger("Main")
    }

    override fun start(primaryStage: Stage) {
        this.primaryStage = primaryStage

        val mainPane = Pane()
        val mainScene = Scene(mainPane, 500.0, 500.0, true)

        val model = tryLoadFromFile()
        if (model != null) modelToGroup(model)?.also { modelGroup ->
            faceCulling?.let { culling ->
                changeFaceCullingRecursively(modelGroup, culling)
            }

            modelGroup.scaleX *= scale
            modelGroup.scaleY *= scale
            modelGroup.scaleZ *= scale

            mainPane.children += modelGroup
        }

        if (planes > 0.0) mainPane.children += createPlanes()

        val perCam = PerspectiveCamera(true)
        configureCamera(perCam)

        camCtrl = FPSCameraController(mainScene, perCam)
        configureController()

        if (light != null) createPointLight()?.let {
            it.translateXProperty().bind(perCam.translateXProperty())
            it.translateYProperty().bind(perCam.translateYProperty())
            it.translateZProperty().bind(perCam.translateZProperty())

            mainPane.children += it
        }

        mainScene.fill = userDefinedBackgroundOrDefault()
        mainScene.camera = perCam

        primaryStage.scene = mainScene
        configurePrimaryStage(primaryStage)
        primaryStage.show()

        if (info && model != null)
            createModelInfoStage(model)
    }

    override fun stop() {
        camCtrl.dispose()
    }

    private fun tryLoadFromFile(): SimpleModel? = try {
        var inputStream: InputStream = Files.newInputStream(input).buffered()
        if (compress) inputStream = GZIPInputStream(inputStream)
        inputStream.use { loadModel(it) }
    } catch (e: NoSuchFileException) {
        logger.warning(NO_SUCH_FILE.format(input))
        e.printStackTrace()
        null
    } catch (e: IOException) {
        logger.warning(FAILED_TO_OPEN_FILE.format(input))
        e.printStackTrace()
        null
    } catch (e: Exception) {
        logger.warning(UNKNOWN_EXCEPTION_ON_OPENING_FILE.format(input))
        e.printStackTrace()
        null
    }

    private fun loadModel(modelStream: InputStream): SimpleModel? = try {
        ModelLoader.load(modelStream)
    } catch (e: IOException) {
        logger.warning(FAILED_TO_LOAD_MODEL)
        e.printStackTrace()
        null
    } catch (e: Exception) {
        logger.warning(UNKNOWN_EXCEPTION_ON_MODEL_LOADING)
        e.printStackTrace()
        null
    }

    private fun modelToGroup(model: SimpleModel): Group? = try {
        FXHandler.load(model) {
            Image(texturesDirectory.resolve(it).toUri().toString())
        }
    } catch (e: Exception) {
        logger.warning(UNKNOWN_EXCEPTION_ON_MODEL_LOADING)
        e.printStackTrace()
        null
    }

    private fun changeFaceCullingRecursively(node: Node, to: CullFace) {
        when (node) {
            is Shape3D -> node.cullFace = to
            is Parent -> node.childrenUnmodifiable.forEach {
                changeFaceCullingRecursively(it, to)
            }
        }
    }

    private fun configureCamera(camera: PerspectiveCamera) {
        camera.fieldOfView = 70.0
        camera.nearClip = 0.1
        camera.farClip = 10000.0
    }

    private fun configureController() {
        camCtrl.accelerationMultiplier = acceleration
        camCtrl.horizontalRotationSpeed = sensitivity
        camCtrl.verticalRotationSpeed = sensitivity
    }

    private fun createPointLight(): PointLight? {
        val color = try {
            Color.web(light)
        } catch (e: IllegalArgumentException) {
            logger.warning(FAILED_TO_PARSE_COLOR.format(light))
            return null
        }

        return PointLight(color)
    }

    private fun userDefinedBackgroundOrDefault() = try {
        Color.web(background)
    } catch (e: IllegalArgumentException) {
        logger.warning(FAILED_TO_PARSE_COLOR.format(background))
        e.printStackTrace()
        Color.web("#414A4C")
    }

    private fun configurePrimaryStage(primaryStage: Stage) {
        primaryStage.title = "SMH Model Viewer"
        primaryStage.minWidth = 200.0
        primaryStage.minHeight = 200.0
        val iconURL = ModelViewer::class.java.getResource("/icon.png").toString()
        primaryStage.icons += Image(iconURL)
    }

    private fun createModelInfoStage(model: SimpleModel) {
        val tree = ModelInfo(model).modelTreeView
        val scene = Scene(tree, 500.0, 500.0)

        val infoStage = Stage()
        infoStage.scene = scene
        infoStage.title = "Model Information"
        infoStage.minWidth = 200.0
        infoStage.minHeight = 200.0
        infoStage.show()
    }

}
