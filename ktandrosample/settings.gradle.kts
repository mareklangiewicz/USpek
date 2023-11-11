@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("../../DepsKt")
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("pl.mareklangiewicz.deps.settings") version "0.2.36"
}

rootProject.name = "ktandrosample"

