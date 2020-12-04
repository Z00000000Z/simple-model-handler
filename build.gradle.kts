import org.gradle.internal.os.OperatingSystem.current

group = "ru.gnylka.smh"
version = "1.2"
description = "A fbx-conv-based tool to handle models"

val kotlinVersion = "1.4.20"
val picocliVersion = "4.5.2"
val gsonVersion = "2.8.6"
val jomlVersion = "1.9.25"
val jfxVersion = "15.0.1"
val jfxPlatform = current().run {
    when {
        isLinux -> "linux"
        isWindows -> "win"
        else -> error("Unknown OS")
    }
}

val kotlinStdlib by extra("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
val picocli by extra("info.picocli:picocli:$picocliVersion")
val gson by extra("com.google.code.gson:gson:$gsonVersion")
val joml by extra("org.joml:joml:$jomlVersion")
val javafxBase by extra("org.openjfx:javafx-base:$jfxVersion:$jfxPlatform")
val javafxGraphics by extra("org.openjfx:javafx-graphics:$jfxVersion:$jfxPlatform")
val javafxControls by extra("org.openjfx:javafx-controls:$jfxVersion:$jfxPlatform")

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.4.20"
}

configure(subprojects) {
    repositories {
        mavenCentral()
    }

    apply(plugin = "java")
    apply(plugin = "kotlin")

    tasks {
        compileJava {
            modularity.inferModulePath.set(true)
            options.compilerArgs.addAll(listOf(
                    "--patch-module",
                    "${project.group}=${project.sourceSets.main.get().output.asPath}"
            ))
            options.release.set(11)
        }

        compileKotlin {
            kotlinOptions.jvmTarget = "11"
            kotlinOptions.noReflect = true
        }

        (findByName("run") as? JavaExec)
                ?.modularity?.inferModulePath?.set(true)

        jar {
            val hierarchy = mutableListOf(project)
            var lastParent = project.parent
            while (true) {
                if (lastParent != rootProject) {
                    hierarchy += lastParent!!
                    lastParent = lastParent?.parent
                } else break
            }

            archiveBaseName.set("smh")
            archiveAppendix.set(hierarchy.map { it.name }.reversed().joinToString("-"))
            archiveVersion.set(rootProject.version as String)
        }
    }
}
