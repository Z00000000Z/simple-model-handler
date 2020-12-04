group = "ru.gnylka.smh.model.fxhandler"

dependencies {
    implementation(project(":model:data"))
    implementation(rootProject.extra["joml"]!!)
    implementation(rootProject.extra["javafxBase"]!!)
    implementation(rootProject.extra["javafxGraphics"]!!)
}

java {
    withSourcesJar()
}
