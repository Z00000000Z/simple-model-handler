plugins {
    application
}

group = "ru.gnylka.smh.testjfx"

val kotlinVersion = rootProject.extra["kotlinVersion"]
val picocliVersion = rootProject.extra["picocliVersion"]
val jomlVersion = rootProject.extra["jomlVersion"]
val jfxVersion = rootProject.extra["jfxVersion"]
val jfxPlatform = rootProject.extra["jfxPlatform"]

dependencies {
    implementation(project(":model:data"))
    implementation(project(":model:loader"))
    implementation(project(":model:fx-handler"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion:modular")
    implementation("info.picocli:picocli:$picocliVersion")
    implementation("org.joml:joml:$jomlVersion")
    implementation("org.openjfx:javafx-base:$jfxVersion:$jfxPlatform")
    implementation("org.openjfx:javafx-graphics:$jfxVersion:$jfxPlatform")
    implementation("org.openjfx:javafx-controls:$jfxVersion:$jfxPlatform")
}

application {
    mainClass.set("ru.gnylka.smh.testjfx.MainKt")
    mainModule.set("ru.gnylka.smh.testjfx")
}
