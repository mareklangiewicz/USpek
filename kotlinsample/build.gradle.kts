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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation(project(":uspek"))
    testImplementation(project(":uspek-junit"))
}

tasks.test {
    useJUnitPlatform()
}
