plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("pl.mareklangiewicz.ktjvmsample.MainKt")
}

repositories {
//    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(project(":uspekx"))
//    testImplementation(Deps.uspekx)
    testImplementation(Deps.junit5)
    testRuntimeOnly(Deps.junit5engine)
}

tasks.test {
    useJUnitPlatform()
}
