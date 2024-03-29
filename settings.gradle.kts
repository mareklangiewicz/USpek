
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    // includeBuild("../DepsKt")
}

plugins {
    id("pl.mareklangiewicz.deps.settings") version "0.2.91" // https://plugins.gradle.org/search?term=mareklangiewicz
    id("com.gradle.enterprise") version "3.16.2" // https://docs.gradle.com/enterprise/gradle-plugin/
}

rootProject.name = "USpek"

include(
    ":uspek",
    ":uspekx",
    ":uspekx-junit4",
    ":uspekx-junit5",
    ":ktmultisample",
    ":ktjunit4sample",
    ":ktjunit5sample",
    ":ktjsreactsample",
    ":ktlinuxsample",
    // ktandrosample is a separate project with own settings (should be opened in Android Studio separately)
    //   it's this way because issues with opening andro projects in Intellij (IDE andro plugin incompatible with new gradle andro plugin)
)
