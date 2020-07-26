package ru.gnylka.smh.model.converter.cli

import java.nio.file.Path

data class FilesData(
        val fromFile: Path,
        val toFile: Path,
        val fileType: OutputFileType?
)
