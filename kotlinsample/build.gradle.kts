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
//    testImplementation(project(":uspek"))
    testImplementation("com.github.langara:USpek:25459c8b62")
}

