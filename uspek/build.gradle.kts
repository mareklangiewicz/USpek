@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.dsl.*
import pl.mareklangiewicz.defaults.*

plugins {
    kotlin("multiplatform") version vers.kotlin
    id("maven-publish")
    id("signing")
}

defaultGroupAndVerAndDescription(libs.USpek)

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
                api(kotlin("test"))
            }
        }
    }
}

defaultPublishing(libs.USpek)

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
