plugins {
    kotlin("js")
}

group = "pl.mareklangiewicz"
version = "0.0.1"

repositories {
    jcenter()
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-js-wrappers")
    maven("https://jitpack.io")

}

dependencies {
//    implementation(project(":uspek"))
    implementation(Deps.uspek)
    implementation("org.jetbrains:kotlin-react:16.13.1-pre.113-kotlin-1.4.0")
    implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.113-kotlin-1.4.0")
    testImplementation(kotlin("test-js"))
}

kotlin {
    js {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
            testTask {
                enabled = System.getenv("JITPACK") != "true"
                useKarma {
                    useChrome()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
}
