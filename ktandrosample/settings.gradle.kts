@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    // includeBuild("../../DepsKt")
}

plugins {
    id("pl.mareklangiewicz.deps.settings") version "0.2.83" // https://plugins.gradle.org/search?term=mareklangiewicz
    id("com.gradle.enterprise") version "3.16.1" // https://docs.gradle.com/enterprise/gradle-plugin/
}

rootProject.name = "ktandrosample"

