group = "ru.gnylka.smh.processing"

val kotlinVersion = rootProject.extra["kotlinVersion"]
val picocliVersion = rootProject.extra["picocliVersion"]
val jomlVersion = rootProject.extra["jomlVersion"]

dependencies {
    implementation(project(":utils"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion:modular")
    implementation("info.picocli:picocli:$picocliVersion")
    implementation("org.joml:joml:$jomlVersion")
}
