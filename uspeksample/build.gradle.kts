plugins {
    kotlin("jvm")
    application
}

application {
    mainClassName = "pl.mareklangiewicz.uspeksample.MainKt"
}

repositories {
    mavenLocal()
//    maven("https://jitpack.io")
}

dependencies {
//    testImplementation(project(":uspek"))
    testImplementation("com.github.langara.uspek:uspek:0.0.10")
}

tasks.test {
    useJUnitPlatform()
}
