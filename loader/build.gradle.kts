group = "ru.gnylka.smh.loader"

dependencies {
    implementation(project(":processing"))
    implementation(rootProject.extra["kotlinStdlib"]!!)
    implementation(rootProject.extra["gson"]!!)
}
