
rootProject.name = "USpek"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    // includeBuild("../DepsKt")
}

plugins {
    id("pl.mareklangiewicz.deps.settings") version "0.2.97" // https://plugins.gradle.org/search?term=mareklangiewicz
    id("com.gradle.develocity") version "3.17.1" // https://docs.gradle.com/enterprise/gradle-plugin/
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        publishing.onlyIf { // careful with publishing fails especially from my machine (privacy)
            true &&
                    it.buildResult.failures.isNotEmpty() &&
                    // it.buildResult.failures.isEmpty() &&
                    System.getenv("GITHUB_ACTIONS") == "true" &&
                    // System.getenv("GITHUB_ACTIONS") != "true" &&
                    true
            // false
        }
    }
}

include(
    ":uspek",
    ":uspekx",
    ":uspekx-junit4",
    ":uspekx-junit5",

    // FIXME NOW:Could not find org.jetbrains.kotlin-wrappers:kotlin-styled:.
    // ":ktjsreactsample",

    // FIXME NOW: Package 'pl.mareklangiewicz.kground' is compiled by a pre-release version of Kotlin and cannot be loaded by this version of the compiler
    // ":ktjunit4sample",
    // ":ktjunit5sample",
    // ":ktmultisample",
    // ":ktlinuxsample",

    // ktandrosample is a separate project with own settings (should be opened in Android Studio separately)
    //   it's this way because issues with opening andro projects in Intellij (IDE andro plugin incompatible with new gradle andro plugin)
)
