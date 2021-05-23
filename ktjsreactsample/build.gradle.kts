plugins {
    kotlin("js")
}

group = USpekKonf.group
version = USpekKonf.verStr

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
    maven("https://jitpack.io")

}

dependencies {
    implementation(project(":uspekx"))
//    implementation(Deps.uspekx)

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
