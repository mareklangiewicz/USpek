@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.evts.*

gradle.logSomeEventsToFile(rootOkioPath / "my.gradle.log")

pluginManagement {
    includeBuild("../deps.kt")
}

plugins {
    id("pl.mareklangiewicz.deps.settings")
}

rootProject.name = "USpek"

include(":uspek", ":uspekx", ":ktjvmsample", "ktjsreactsample", ":ktlinuxsample")
