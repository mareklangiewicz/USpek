@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("../deps.kt")
}

plugins {
    id("pl.mareklangiewicz.deps.settings")
}

rootProject.name = "USpek"

include(":uspek", ":uspekx", ":ktjvmsample", "ktjsreactsample")
//include(":uspek", ":uspekx", ":ktjvmsample", "ktjsreactsample", ":ktlinuxsample")
