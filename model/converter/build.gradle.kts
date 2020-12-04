group = "ru.gnylka.smh.model.converter"

dependencies {
    implementation(project(":model:data"))
    implementation(project(":processing"))
    implementation(project(":utils"))
    implementation(rootProject.extra["kotlinStdlib"]!!)
    implementation(rootProject.extra["joml"]!!)
}
