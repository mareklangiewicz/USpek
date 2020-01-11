plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "pl.mareklangiewicz.uspek.MainKt"
}

dependencies {
    implementation(Deps.kotlinStdlib8)
    testImplementation(project(":uspek"))
    testImplementation(project(":uspek-junit5"))
}

tasks.test {
    useJUnitPlatform()
}
