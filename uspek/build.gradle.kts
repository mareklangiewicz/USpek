plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

defaultGroupAndVer(Deps.uspek)
description = USpekKonf.description

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js(IR) {
        nodejs()
        browser {
            testTask {
                enabled = System.getenv("JITPACK") != "true"
            }
        }
    }
    linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val linuxX64Main by getting
    }
}
