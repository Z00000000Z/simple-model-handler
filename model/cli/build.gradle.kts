plugins {
    application
}

group = "ru.gnylka.smh.model.converter.cli"

dependencies {
    implementation(project(":loader"))
    implementation(project(":model:converter"))
    implementation(project(":model:data"))
    implementation(project(":processing"))
    implementation(project(":utils"))
    implementation(rootProject.extra["kotlinStdlib"]!!)
    implementation(rootProject.extra["picocli"]!!)
}

application {
    mainClass.set("ru.gnylka.smh.model.converter.cli.MainKt")
    mainModule.set("ru.gnylka.smh.model.converter.cli")
}

tasks.named("run", JavaExec::class) {
    modularity.inferModulePath.set(true)
    systemProperty("ru.gnylka.smh.loader.fbxconv.startFromPath", "true")
}
