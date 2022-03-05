plugins {
    kotlin("multiplatform") version vers.kotlin
}

repositories {
//    mavenLocal()
    maven("https://jitpack.io")
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "pl.mareklangiewicz.ktlinuxsample.main"
            }
        }
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(project(":uspek"))
//                implementation(deps.uspek)
            }
        }
        val nativeMain by getting
        val nativeTest by getting
    }
}


