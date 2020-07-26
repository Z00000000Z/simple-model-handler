group = "ru.gnylka.smh.model.fxhandler"

val jomlVersion = rootProject.extra["jomlVersion"]
val jfxVersion = rootProject.extra["jfxVersion"]
val jfxPlatform = rootProject.extra["jfxPlatform"]

dependencies {
    implementation(project(":model:data"))
    implementation("org.joml:joml:$jomlVersion")
    implementation("org.openjfx:javafx-base:$jfxVersion:$jfxPlatform")
    implementation("org.openjfx:javafx-graphics:$jfxVersion:$jfxPlatform")
}

tasks {
    java {
        withSourcesJar()
    }
}
