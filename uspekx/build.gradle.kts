import pl.mareklangiewicz.defaults.*
import org.jetbrains.kotlin.gradle.dsl.*

plugins {
    kotlin("multiplatform") version vers.kotlin
    id("maven-publish")
    id("signing")
}

defaultGroupAndVer(deps.uspekx)
description = USpekKonf.description

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    jsDefault(withNode = true)
    linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":uspek"))
                api(deps.kotlinxCoroutinesCore)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(deps.junit5)
            }
        }
    }
}

defaultUSpekPublishing()

defaultSigning()

// TODO NOW: injecting (like Andro Build Template)

// region Kotlin Multi Template

fun KotlinMultiplatformExtension.jsDefault(
    withBrowser: Boolean = true,
    withNode: Boolean = false,
    testWithChrome: Boolean = true,
    testHeadless: Boolean = true,
) {
    js(IR) {
        if (withBrowser) browser {
            testTask {
                useKarma {
                    when (testWithChrome to testHeadless) {
                        true to true -> useChromeHeadless()
                        true to false -> useChrome()
                    }
                }
            }
        }
        if (withNode) nodejs()
    }
}

// endregion Kotlin Multi Template
