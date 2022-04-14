import pl.mareklangiewicz.utils.*

plugins {
    kotlin("jvm")
    application
}

application {
    mainClass put "pl.mareklangiewicz.ktjvmsample.MainKt"
}

repositories {
//    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(project(":uspekx"))
//    testImplementation(deps.uspekx)
    testImplementation(deps.junit5)
    testRuntimeOnly(deps.junit5engine)
}

tasks.defaultKotlinCompileOptions()

tasks.test {
    useJUnitPlatform()
}

// region [Kotlin Module Build Template]

fun TaskCollection<Task>.defaultKotlinCompileOptions(
    jvmTargetVer: String = vers.defaultJvm,
    requiresOptIn: Boolean = true
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = jvmTargetVer
        if (requiresOptIn) freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}

// endregion [Kotlin Module Build Template]
