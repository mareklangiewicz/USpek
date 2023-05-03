@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.evts.*
import pl.mareklangiewicz.utils.*

//gradle.logSomeEventsToFile(rootProjectPath / "my.gradle.log")

pluginManagement {
    includeBuild("../DepsKt")
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
)
