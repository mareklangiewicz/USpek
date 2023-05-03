@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.evts.*
import pl.mareklangiewicz.utils.*

//gradle.logSomeEventsToFile(rootProjectPath / "my.gradle.log")

pluginManagement {
//    includeBuild("../DepsKt")
    repositories {
        google()
        gradlePluginPortal()
    }
}

plugins {
    id("pl.mareklangiewicz.deps.settings") version "0.2.36"
}

rootProject.name = "USpek"

include(
    ":uspek",
    ":uspekx",
    ":uspekx-junit4",
    ":uspekx-junit5",
    ":ktjunit4sample",
    ":ktjunit5sample",
    ":ktjsreactsample",
    ":ktlinuxsample",
    // ktandrosample is a separate project with own settings (should be opened in Android Studio separately)
    //   it's this way because issues with opening andro projects in Intellij (IDE andro plugin incompatible with new gradle andro plugin)
)
