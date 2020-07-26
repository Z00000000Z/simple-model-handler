package ru.gnylka.smh.loader.fbxconv

import ru.gnylka.smh.loader.LoadingException
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

class FbxConvWrapper(
        private val inputFileString: String,
        private var flipTexCoords: Boolean = true
) {

    private val startFromPath = System.getProperty(
            "ru.gnylka.smh.loader.fbxconv.startFromPath", "false")!!.toBoolean()

    private val osName = System.getProperty("os.name")!!

    fun startConverter(): Path {
        val tempFilePath = Files.createTempFile("FbxConvWrapper", null)
        val tempFileString = tempFilePath.toAbsolutePath().toString()

        launchProcess(tempFileString)

        return tempFilePath
    }

    private fun launchProcess(tempFileString: String) {
        val prefix = getCommandPrefix()
        val commandName =
                if (isWindows()) "${prefix}fbx-conv.exe"
                else "${prefix}fbx-conv"

        val command = mutableListOf(commandName, "-o", "g3dj")
        if (flipTexCoords) command.add("-f")
        command.add(inputFileString)
        command.add(tempFileString)

        val processBuilder = ProcessBuilder(command)
        // tells fbx-conv to search for libfbxsdk.so
        // in the current directory (user.dir) on linux
        if (isLinux() && !startFromPath)
            processBuilder.environment()["LD_LIBRARY_PATH"] = prefix

        val process = processBuilder.start()
        process.waitFor(10L, TimeUnit.SECONDS)
        if (process.exitValue() != 0)
            throw LoadingException("fbx-conv returned a non-zero exit value")
    }

    private fun isWindows() = osName.startsWith("windows", ignoreCase = true)

    private fun isLinux() = osName.startsWith("linux", ignoreCase = true)

    private fun getCommandPrefix() =
            if (startFromPath) ""
            else System.getProperty("java.home") + '/'

}
