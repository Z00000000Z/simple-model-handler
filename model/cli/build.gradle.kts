plugins {
    application
}

group = "ru.gnylka.smh.model.converter.cli"

val kotlinVersion = rootProject.extra["kotlinVersion"]
val picocliVersion = rootProject.extra["picocliVersion"]

dependencies {
    implementation(project(":loader"))
    implementation(project(":model:converter"))
    implementation(project(":model:data"))
    implementation(project(":processing"))
    implementation(project(":utils"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion:modular")
    implementation("info.picocli:picocli:$picocliVersion")
}

application {
    mainClass.set("ru.gnylka.smh.model.converter.cli.MainKt")
    mainModule.set("ru.gnylka.smh.model.converter.cli")
}

tasks {
    run.get().apply {
        systemProperty("ru.gnylka.smh.loader.fbxconv.startFromPath", "true")
    }
}
