plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

group = "com.github.langara.uspek"
version = "0.0.14"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js {
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
                api(Deps.kotlinTestCommon)
                api(Deps.kotlinTestAnnotationsCommon)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(Deps.kotlinTestJUnit)
                implementation(Deps.junit5)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(Deps.kotlinTestJs)
            }
        }
        val linuxX64Main by getting
    }
}

