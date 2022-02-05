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
                useKarma {
                    useChromeHeadless()
                }
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
    }
}
