plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
}

defaultGroupAndVer(Deps.uspek)
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
                api(kotlin("test"))
            }
        }
    }
}

defaultUSpekPublishing()

defaultSigning()
