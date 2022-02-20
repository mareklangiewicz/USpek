import pl.mareklangiewicz.defaults.*

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
}

defaultGroupAndVer(Deps.uspekx)
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
                api(Deps.kotlinxCoroutinesCore)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(Deps.junit5)
            }
        }
    }
}

defaultUSpekPublishing()

defaultSigning()
