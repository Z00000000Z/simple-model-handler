plugins {
    application
}

group = "ru.gnylka.smh.testjfx"

dependencies {
    implementation(project(":model:data"))
    implementation(project(":model:loader"))
    implementation(project(":model:fx-handler"))
    implementation(rootProject.extra["kotlinStdlib"]!!)
    implementation(rootProject.extra["picocli"]!!)
    implementation(rootProject.extra["joml"]!!)
    implementation(rootProject.extra["javafxBase"]!!)
    implementation(rootProject.extra["javafxGraphics"]!!)
    implementation(rootProject.extra["javafxControls"]!!)
}

application {
    mainClass.set("ru.gnylka.smh.testjfx.MainKt")
    mainModule.set("ru.gnylka.smh.testjfx")
}
