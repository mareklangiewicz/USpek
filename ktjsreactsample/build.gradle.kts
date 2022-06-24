import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.dsl.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.defaults.*

plugins {
    kotlin("multiplatform") version vers.kotlin17 // with kotlin16 I get task jsBrowserDevelopmentRun SKIPPED
}

defaultBuildTemplateForMppApp(
    appMainPackage = "pl.mareklangiewicz.ktjsreactsample",
    withJvm = false,
    withJs = true,
    withNativeLinux64 = false,
    withTestUSpekX = true,
)

kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(deps.uspekx)
                implementation(deps.kotlinxCoroutinesCore)
                implementation(project.dependencies.enforcedPlatform(deps.kotlinJsWrappersBoM))
                implementation(deps.kotlinJsWrappersReact)
                implementation(deps.kotlinJsWrappersReactDom)
                implementation(deps.kotlinJsWrappersStyled)
                implementation(npm("react", vers.npmReact))
                implementation(npm("react-dom", vers.npmReact))
                implementation(npm("styled-components", vers.npmStyled))
            }
        }
    }
}

// Fixes webpack-cli incompatibility by pinning the newest version.
// https://stackoverflow.com/questions/72731436/kotlin-multiplatform-gradle-task-jsrun-gives-error-webpack-cli-typeerror-c/72731728
rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0"
}


// region [Kotlin Module Build Template]

fun TaskCollection<Task>.defaultKotlinCompileOptions(
    jvmTargetVer: String = vers.defaultJvm,
    requiresOptIn: Boolean = true
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = jvmTargetVer
        if (requiresOptIn) freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}

// endregion [Kotlin Module Build Template]

// region [MPP Module Build Template]

/** Only for very standard small libs. In most cases it's better to not use this function. */
fun Project.defaultBuildTemplateForMppLib(
    details: LibDetails = libs.Unknown,
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withComposeJbDevRepo: Boolean = false,
    withTestJUnit4: Boolean = false,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    repositories { defaultRepos(withKotlinxHtml = withKotlinxHtml, withComposeJbDev = withComposeJbDevRepo) }
    defaultGroupAndVerAndDescription(details)
    kotlin { allDefault(
        withJvm,
        withJs,
        withNativeLinux64,
        withKotlinxHtml,
        withTestJUnit4,
        withTestJUnit5,
        withTestUSpekX,
        addCommonMainDependencies
    ) }
    tasks.defaultKotlinCompileOptions()
    tasks.defaultTestsOptions(onJvmUseJUnitPlatform = withTestJUnit5)
    if (plugins.hasPlugin("maven-publish")) {
        defaultPublishing(details)
        if (plugins.hasPlugin("signing")) defaultSigning()
        else println("MPP Module ${name}: signing disabled")
    }
    else println("MPP Module ${name}: publishing (and signing) disabled")
}

/** Only for very standard small libs. In most cases it's better to not use this function. */
@Suppress("UNUSED_VARIABLE")
fun KotlinMultiplatformExtension.allDefault(
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withTestJUnit4: Boolean = false,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    if (withJvm) jvm()
    if (withJs) jsDefault()
    if (withNativeLinux64) linuxX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                if (withKotlinxHtml) implementation(deps.kotlinxHtml)
                addCommonMainDependencies()
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                if (withTestUSpekX) implementation(deps.uspekx)
            }
        }
        if (withJvm) {
            val jvmTest by getting {
                dependencies {
                    if (withTestJUnit4) implementation(deps.junit4)
                    if (withTestJUnit5) implementation(deps.junit5engine)
                    if (withTestUSpekX) {
                        implementation(deps.uspekx)
                        if (withTestJUnit4) implementation(deps.uspekxJUnit4)
                        if (withTestJUnit5) implementation(deps.uspekxJUnit5)
                    }
                }
            }
        }
        if (withNativeLinux64) {
            val linuxX64Main by getting
            val linuxX64Test by getting
        }
    }
}


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

// endregion [MPP Module Build Template]

// region [MPP App Build Template]

fun Project.defaultBuildTemplateForMppApp(
    appMainPackage: String,
    appMainFun: String = "main",
    details: LibDetails = libs.Unknown,
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withComposeJbDevRepo: Boolean = false,
    withTestJUnit4: Boolean = false,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    defaultBuildTemplateForMppLib(
        details = details,
        withJvm = withJvm,
        withJs = withJs,
        withNativeLinux64 = withNativeLinux64,
        withKotlinxHtml = withKotlinxHtml,
        withComposeJbDevRepo = withComposeJbDevRepo,
        withTestJUnit4 = withTestJUnit4,
        withTestJUnit5 = withTestJUnit5,
        withTestUSpekX = withTestUSpekX,
        addCommonMainDependencies = addCommonMainDependencies
    )
    kotlin {
        if (withJvm) jvm {
            println("MPP App ${project.name}: Generating general jvm executables with kotlin multiplatform plugin is not supported (without compose).")
            // TODO_someday: Will they support multiplatform way of declaring jvm app?
            //binaries.executable()
        }
        if (withJs) js(IR) {
            binaries.executable()
        }
        if (withNativeLinux64) linuxX64 {
            binaries {
                executable {
                    entryPoint = "$appMainPackage.$appMainFun"
                }
            }
        }
    }
}

// endregion [MPP App Build Template]