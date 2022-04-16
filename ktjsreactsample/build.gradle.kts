import pl.mareklangiewicz.defaults.*

plugins {
    kotlin("js")
}

defaultGroupAndVerAndDescription(libs.USpek)

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("test"))
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
