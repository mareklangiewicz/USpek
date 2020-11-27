plugins {
    kotlin("jvm")
    application
}

application {
    mainClassName = "pl.mareklangiewicz.uspeksample.MainKt"
}

repositories {
//    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(project(":uspek"))
//    testImplementation(Deps.uspek)
    testImplementation(Deps.junit5)
    testImplementation(Deps.junit5engine)
}

tasks.test {
    useJUnitPlatform()
}
