package ru.gnylka.smh.model.converter.cli

enum class OutputFileType(
        val fileExtension: String
) {

    BINARY("smhmb"),
    TEXT("smhmt")

}
