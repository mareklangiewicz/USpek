@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.evts.*
import pl.mareklangiewicz.utils.*

gradle.logSomeEventsToFile(rootProjectPath / "my.gradle.log")

pluginManagement {
    includeBuild("../deps.kt")
}

plugins {
    id("pl.mareklangiewicz.deps.settings")
}

rootProject.name = "USpek"

include(":uspek", ":uspekx", ":ktjvmsample", "ktjsreactsample", ":ktlinuxsample")
