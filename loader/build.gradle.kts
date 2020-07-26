group = "ru.gnylka.smh.loader"

val kotlinVersion = rootProject.extra["kotlinVersion"]
val gsonVersion = rootProject.extra["gsonVersion"]

dependencies {
    implementation(project(":processing"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion:modular")
    implementation("com.google.code.gson:gson:$gsonVersion")
}
