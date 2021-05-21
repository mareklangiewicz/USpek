plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

group = "com.github.langara.uspek"
version = "0.0.16"

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
                api(Deps.kotlinxCoroutinesCore)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(Deps.junit5)
            }
        }
        val jsMain by getting
        val linuxX64Main by getting
    }
}
