package ru.gnylka.smh.model.converter.cli

fun getExtension(fileName: String) = fileName.substringAfterLast('.')

fun getFileNameWithoutExtension(fileName: String) = fileName.substringBeforeLast('.')
