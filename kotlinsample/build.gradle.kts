import org.gradle.kotlin.dsl.kotlin
import java.net.URI

plugins {
    kotlin("jvm", "1.1.50")
    application
}

application {
    mainClassName = "pl.mareklangiewicz.uspek.MainKt"
}

repositories {
    jcenter()
    mavenCentral()
    maven { url = URI("https://jitpack.io") }
}

dependencies {
    implementation(kotlin("stdlib", "1.1.50"))
    implementation("junit:junit:4.12")
    testImplementation("com.github.langara:USpek:b89f82be9b")
}

