import java.net.URI

plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "pl.mareklangiewicz.uspek.MainKt"
}

dependencies {
    implementation(Deps.kotlinStdlib8)
    implementation(Deps.junit)
    implementation(Deps.kotlinxCoroutinesCore)
    testImplementation(project(":uspek"))
    testImplementation(project(":uspek-junit"))
}

