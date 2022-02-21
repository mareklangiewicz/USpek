import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.sourcefun.*
import org.jetbrains.kotlin.gradle.dsl.*

plugins {
    kotlin("multiplatform") version Vers.kotlin
    id("maven-publish")
    id("signing")
    id("pl.mareklangiewicz.sourcefun")
}

defaultGroupAndVer(Deps.uspek)
description = USpekKonf.description

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    jsDefault(withNode = true)
    linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("test"))
            }
        }
    }
}

defaultUSpekPublishing()

defaultSigning()


// TODO NOW: temporary experiment of how I can use processEachFile to do sth like SourceFun, then extract boilerplate
// to dsl like tmpFunTask by tasks.registeringSourceFun {...processing...}
// what about registerAllThatGroupFun?? maybe sth like this would be better than sourceFun DSL????
val tmpFunTask by tasks.registering(SourceFunTask::class) {

}

sourceFun {
    def("someTask1", "someInDir", "someOutdir") { "[BEGIN]\n$it[END]\n" }
}

// region Kotlin Multi Template

fun KotlinMultiplatformExtension.jsDefault(
    withBrowser: Boolean = true,
    withNode: Boolean = false,
    testWithChrome: Boolean = true,
    testHeadless: Boolean = true,
) {
    js(IR) {
        if (withBrowser) browser {
            testTask {
                useKarma {
                    when (testWithChrome to testHeadless) {
                        true to true -> useChromeHeadless()
                        true to false -> useChrome()
                    }
                }
            }
        }
        if (withNode) nodejs()
    }
}

// endregion Kotlin Multi Template
