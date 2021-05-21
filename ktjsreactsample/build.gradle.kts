plugins {
    kotlin("js")
}

group = "pl.mareklangiewicz"
version = "0.0.02"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
    maven("https://jitpack.io")

}

dependencies {
    implementation(project(":uspek"))
//    implementation(Deps.uspek)

    implementation(Deps.kotlinxCoroutinesCore)

    implementation(Deps.kotlinJsWrappersReact)
    implementation(Deps.kotlinJsWrappersReactDom)
    implementation(Deps.kotlinJsWrappersStyled)

    implementation(npm("react", Vers.npmReact))
    implementation(npm("react-dom", Vers.npmReact))
    implementation(npm("styled-components", Vers.npmStyled))
}

kotlin {
    js(IR) {
        // FIXME: getting runtime(browser) errors and empty blue page when using js(IR) here and in ktjsreactsample
        //   (check again after some deps updates)

        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
            testTask {
                enabled = System.getenv("JITPACK") != "true"
                useKarma {
                    useChrome()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
}
