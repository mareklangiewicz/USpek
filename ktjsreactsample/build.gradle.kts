import pl.mareklangiewicz.defaults.*

plugins {
    kotlin("js") version vers.kotlin
}

defaultGroupAndVer(deps.uspek)

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
    maven("https://jitpack.io")

}

dependencies {
    implementation(project(":uspekx"))
//    implementation(deps.uspekx)

    implementation(deps.kotlinxCoroutinesCore)

    implementation(enforcedPlatform(deps.kotlinJsWrappersBoM))
    implementation(deps.kotlinJsWrappersReact)
    implementation(deps.kotlinJsWrappersReactDom)
    implementation(deps.kotlinJsWrappersStyled)

    implementation(npm("react", vers.npmReact))
    implementation(npm("react-dom", vers.npmReact))
    implementation(npm("styled-components", vers.npmStyled))
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
//                    useChrome()
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
}
