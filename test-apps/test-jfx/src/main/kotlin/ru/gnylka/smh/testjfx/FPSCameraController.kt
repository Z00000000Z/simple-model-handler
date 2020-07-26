package ru.gnylka.smh.testjfx

import javafx.animation.AnimationTimer
import javafx.event.EventHandler
import javafx.geometry.Point3D
import javafx.scene.Cursor
import javafx.scene.PerspectiveCamera
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.robot.Robot
import javafx.scene.transform.Rotate
import org.joml.Vector3d
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class FPSCameraController(
        private val scene: Scene,
        private val perCam: PerspectiveCamera
) {

    var targetFramerate = 1.0 / 60.0
    var forwardSpeed = 30.0
    var sideSpeed = forwardSpeed / 2
    var verticalSpeed = forwardSpeed
    var accelerationMultiplier = 2.0
    var horizontalRotationSpeed = 0.25
    var verticalRotationSpeed = horizontalRotationSpeed
    var isEnabled = true
        set(value) {
            if (value) updater.start()
            else updater.stop()

            field = value
        }

    private var updater: Updater

    private var mouseMoveMode = false
    private var forwardMoving = 0.0
    private var sideMoving = 0.0
    private var verticalMoving = 0.0
    private var isAccelerated = false

    private val cameraRotatePitch = Rotate(0.0, Point3D(0.0, 1.0, 0.0))
    private val cameraRotateYaw = Rotate(180.0, Point3D(-1.0, 0.0, 0.0))
    private var previousX = Double.NaN
    private var previousY = Double.NaN
    private var anglesDirty = true
    private val sceneRobot = Robot()
    private var isRobotMove = false

    private val keyPressedHanlder = { event: KeyEvent ->
        when (event.code) {
            KeyCode.W -> if (forwardMoving == 0.0) forwardMoving = 1.0
            KeyCode.S -> if (forwardMoving == 0.0) forwardMoving = -1.0
            KeyCode.D -> if (sideMoving == 0.0) sideMoving = 1.0
            KeyCode.A -> if (sideMoving == 0.0) sideMoving = -1.0
            KeyCode.SPACE -> if (verticalMoving == 0.0) verticalMoving = 1.0
            KeyCode.SHIFT -> if (verticalMoving == 0.0) verticalMoving = -1.0
            KeyCode.CONTROL -> isAccelerated = true
            else -> Unit
        }
    }

    private val keyReleasedHandler = { event: KeyEvent ->
        when (event.code) {
            KeyCode.W -> if (forwardMoving == 1.0) forwardMoving = 0.0
            KeyCode.S -> if (forwardMoving == -1.0) forwardMoving = 0.0
            KeyCode.D -> if (sideMoving == 1.0) sideMoving = 0.0
            KeyCode.A -> if (sideMoving == -1.0) sideMoving = 0.0
            KeyCode.SPACE -> if (verticalMoving == 1.0) verticalMoving = 0.0
            KeyCode.SHIFT -> if (verticalMoving == -1.0) verticalMoving = 0.0
            KeyCode.M -> {
                mouseMoveMode = !mouseMoveMode
                setPreviousNaN()
                scene.cursor =
                        if (mouseMoveMode) Cursor.NONE
                        else Cursor.DEFAULT
            }
            KeyCode.P -> isEnabled = !isEnabled
            KeyCode.R -> {
                perCam.translateX = 0.0
                perCam.translateY = 0.0
                perCam.translateZ = 0.0
            }
            KeyCode.CONTROL -> isAccelerated = false
            else -> Unit
        }
    }

    private val mouseDraggedHandler = EventHandler<MouseEvent> { event ->
        handleMousePositionChange(event)
        previousX = event.screenX
        previousY = event.screenY
    }

    private val mouseMovedHandler = EventHandler<MouseEvent> { event ->
        if (!mouseMoveMode) return@EventHandler

        if (isRobotMove) {
            isRobotMove = false
            return@EventHandler
        }

        val window = scene.window ?: throw NullPointerException("Scene must have parent window")
        val xMousePosition = window.x + window.width * 0.5
        val yMousePosition = window.y + window.height * 0.5
        previousX = xMousePosition
        previousY = yMousePosition

        handleMousePositionChange(event)

        sceneRobot.mouseMove(xMousePosition, yMousePosition)
        isRobotMove = true
    }

    private val mouseReleasedHandler = EventHandler<MouseEvent> {
        if (mouseMoveMode) return@EventHandler

        setPreviousNaN()
    }

    private val scrollHandler = EventHandler<ScrollEvent> {
        forwardSpeed += it.deltaY * 0.125
        forwardSpeed = max(forwardSpeed, 0.0)
        sideSpeed = forwardSpeed / 2
        verticalSpeed = forwardSpeed
    }

    init {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyPressedHanlder)
        scene.addEventHandler(KeyEvent.KEY_RELEASED, keyReleasedHandler)

        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler)
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler)
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler)

        scene.addEventFilter(ScrollEvent.SCROLL, scrollHandler)

        perCam.transforms.addAll(cameraRotatePitch, cameraRotateYaw)

        updater = Updater()
        updater.start()
    }

    fun dispose() {
        scene.removeEventHandler(KeyEvent.KEY_PRESSED, keyPressedHanlder)
        scene.removeEventHandler(KeyEvent.KEY_RELEASED, keyReleasedHandler)

        scene.removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler)
        scene.removeEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler)
        scene.removeEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler)

        scene.removeEventFilter(ScrollEvent.SCROLL, scrollHandler)

        perCam.transforms.removeAll(cameraRotatePitch, cameraRotateYaw)
    }

    // ----- Viewing logic -----

    private fun handleMousePositionChange(event: MouseEvent) {
        val currentX = event.screenX
        val currentY = event.screenY
        if (!previousX.isNaN() && !previousY.isNaN())
            handleCameraRotation(currentX - previousX, currentY - previousY)
    }

    private fun handleCameraRotation(deltaX: Double, deltaY: Double) {
        anglesDirty = true

        var pitch = cameraRotatePitch.angle
        var yaw = cameraRotateYaw.angle

        pitch -= deltaX * horizontalRotationSpeed
        yaw += deltaY * verticalRotationSpeed

        pitch %= 360.0
        if (yaw < 95) yaw = 95.0
        else if (yaw > 265) yaw = 265.0

        cameraRotatePitch.angle = pitch
        cameraRotateYaw.angle = yaw
    }

    private fun setPreviousNaN() {
        previousX = Double.NaN
        previousY = Double.NaN
    }

    // ----- Moving logic -----

    private val up = Vector3d(0.0, 1.0, 0.0)
    private var totalDelta = 0.0
    private var xDirection = 0.0
    private var yDirection = 0.0
    private var zDirection = 0.0
    private val xyPlaneVector = Vector3d()

    private fun update(delta: Double) {
        totalDelta += delta
        if (totalDelta >= targetFramerate) {
            totalDelta -= targetFramerate

            val acceleration =
                    if (isAccelerated) accelerationMultiplier
                    else 1.0
            val ta = targetFramerate * acceleration

            val forwardMultiplier = forwardMoving * forwardSpeed * ta
            val sideMultiplier = sideMoving * sideSpeed * ta
            val verticalMultiplier = verticalMoving * verticalSpeed * ta

            perCam.translateY = perCam.translateY + verticalMultiplier

            if (forwardMultiplier == 0.0 && sideMultiplier == 0.0) return

            updateCameraDirection()
            perCam.translateX += forwardMultiplier * xDirection
            perCam.translateY += forwardMultiplier * yDirection
            perCam.translateZ += forwardMultiplier * zDirection

            xyPlaneVector.set(xDirection, 0.0, zDirection).normalize()
            xyPlaneVector.cross(up)

            perCam.translateX += sideMultiplier * xyPlaneVector.x
            perCam.translateY += sideMultiplier * xyPlaneVector.y
            perCam.translateZ += sideMultiplier * xyPlaneVector.z
        }
    }

    private fun updateCameraDirection() {
        if (anglesDirty) {
            val pitchRad = toRadians(cameraRotatePitch.angle)
            val yawRad = toRadians(cameraRotateYaw.angle)
            val yawCos = cos(yawRad)
            xDirection = sin(pitchRad) * yawCos
            zDirection = cos(pitchRad) * yawCos
            yDirection = sin(yawRad)
            anglesDirty = false
        }
    }

    private inner class Updater : AnimationTimer() {

        private var previousTime: Long = -1L

        override fun start() {
            super.start()
            previousTime = System.nanoTime()
        }

        override fun handle(now: Long) {
            val elapsed = now - previousTime
            val elapsedSeconds = elapsed * 1e-9

            update(elapsedSeconds)

            previousTime = now
        }

    }

}
