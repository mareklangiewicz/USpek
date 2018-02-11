import java.net.URI

@Suppress("UNCHECKED_CAST")
val deps = rootProject.ext.properties["deps"] as Map<String, Map<String, String>>
// TODO: find in kotlin-dsl repo proper syntax to use ext properties in build.gradle.kts

plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "pl.mareklangiewicz.uspek.MainKt"
}

dependencies {
    implementation(deps["kotlinStdlib"]!!)
    implementation(deps["junit"]!!)
//    testImplementation("com.github.langara:USpek:b89f82be9b")
    testImplementation(project(":uspek"))
}

