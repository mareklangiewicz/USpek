plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

defaultGroupAndVer(Deps.uspekx)
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
