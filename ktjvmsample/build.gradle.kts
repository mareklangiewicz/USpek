import pl.mareklangiewicz.utils.*

plugins {
    kotlin("jvm") version vers.kotlin
    application
}

application {
    mainClass put "pl.mareklangiewicz.ktjvmsample.MainKt"
}

repositories {
//    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(project(":uspekx"))
//    testImplementation(deps.uspekx)
    testImplementation(deps.junit5)
    testRuntimeOnly(deps.junit5engine)
}

tasks.test {
    useJUnitPlatform()
}
