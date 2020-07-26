group = "ru.gnylka.smh.model.converter"

val kotlinVersion = rootProject.extra["kotlinVersion"]
val jomlVersion = rootProject.extra["jomlVersion"]

dependencies {
    implementation(project(":model:data"))
    implementation(project(":processing"))
    implementation(project(":utils"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion:modular")
    implementation("org.joml:joml:$jomlVersion")
}
