group = "ru.gnylka.smh.processing"

dependencies {
    implementation(project(":utils"))
    implementation(rootProject.extra["kotlinStdlib"]!!)
    implementation(rootProject.extra["picocli"]!!)
    implementation(rootProject.extra["joml"]!!)
}
