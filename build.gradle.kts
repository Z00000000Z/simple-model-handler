import org.gradle.internal.os.OperatingSystem

group = "ru.gnylka.smh"
version = "1.1"

val kotlinVersion by rootProject.extra("1.3.72")
val picocliVersion by rootProject.extra("4.4.0")
val gsonVersion by rootProject.extra("2.8.6")
val jomlVersion by rootProject.extra("1.9.25")
val jfxVersion by rootProject.extra("14.0.2.1")
val jfxPlatform by rootProject.extra(getPlatformName())

fun getPlatformName(): String {
    val os = OperatingSystem.current()
    return when {
        os.isLinux -> "linux"
        os.isWindows -> "win"
        os.isMacOsX -> "mac"
        else -> error("Unknown OS")
    }
}

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.3.72"
    id("org.javamodularity.moduleplugin") version "1.7.0" apply false
}

configure(subprojects) {
    repositories {
        mavenCentral()
        jcenter()
    }

    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "org.javamodularity.moduleplugin")

    tasks {
        compileJava {
            options.apply {
                sourceCompatibility = "11"
                targetCompatibility = "11"
            }
        }

        compileKotlin {
            kotlinOptions {
                jvmTarget = "11"
                noReflect = true
            }
        }

        jar {
            val hierarchy = mutableListOf(project)
            while (true) {
                val lastParent = hierarchy.last().parent
                if (lastParent != null && lastParent != rootProject)
                    hierarchy += lastParent
                else break
            }

            archiveBaseName.set("smh")
            archiveAppendix.set(hierarchy.map { it.name }.reversed().joinToString("-"))
            archiveVersion.set(rootProject.version as String)
        }
    }
}
