group = "ru.gnylka.smh.model.loader"

dependencies {
    implementation(project(":model:data"))
}

tasks {
    java {
        withSourcesJar()
    }
}
