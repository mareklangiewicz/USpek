import org.jetbrains.kotlin.gradle.plugin.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

plugins {
    kotlin("jvm")
    application
}

defaultBuildTemplateForJvmApp(appMainPackage = "pl.mareklangiewicz.ktjvmsample")

// region [Kotlin Module Build Template]

fun RepositoryHandler.defaultRepos(
    withMavenLocal: Boolean = false,
    withMavenCentral: Boolean = true,
    withGradle: Boolean = false,
    withGoogle: Boolean = true,
    withKotlinx: Boolean = true,
    withKotlinxHtml: Boolean = false,
    withComposeJbDev: Boolean = false,
    withComposeCompilerAndroidxDev: Boolean = false,
    withKtorEap: Boolean = false,
    withJitpack: Boolean = false,
) {
    if (withMavenLocal) mavenLocal()
    if (withMavenCentral) mavenCentral()
    if (withGradle) gradlePluginPortal()
    if (withGoogle) google()
    if (withKotlinx) maven(repos.kotlinx)
    if (withKotlinxHtml) maven(repos.kotlinxHtml)
    if (withComposeJbDev) maven(repos.composeJbDev)
    if (withComposeCompilerAndroidxDev) maven(repos.composeCompilerAndroidxDev)
    if (withKtorEap) maven(repos.ktorEap)
    if (withJitpack) maven(repos.jitpack)
}

fun TaskCollection<Task>.defaultKotlinCompileOptions(
    jvmTargetVer: String = vers.defaultJvm,
    requiresOptIn: Boolean = true
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = jvmTargetVer
        if (requiresOptIn) freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}

fun TaskCollection<Task>.defaultTestsOptions(
    printStandardStreams: Boolean = true,
    printStackTraces: Boolean = true,
    onJvmUseJUnitPlatform: Boolean = true,
) = withType<AbstractTestTask>().configureEach {
    testLogging {
        showStandardStreams = printStandardStreams
        showStackTraces = printStackTraces
    }
    if (onJvmUseJUnitPlatform) (this as? Test)?.useJUnitPlatform()
}

// Provide artifacts information requited by Maven Central
fun MavenPublication.defaultPOM(lib: LibDetails) = pom {
    name put lib.name
    description put lib.description
    url put lib.githubUrl

    licenses {
        license {
            name put lib.licenceName
            url put lib.licenceUrl
        }
    }
    developers {
        developer {
            id put lib.authorId
            name put lib.authorName
            email put lib.authorEmail
        }
    }
    scm { url put lib.githubUrl }
}

/** See also: root project template-mpp: fun Project.defaultSonatypeOssStuffFromSystemEnvs */
fun Project.defaultSigning(
    keyId: String = rootExt("signing.keyId"),
    key: String = rootExt("signing.key"),
    password: String = rootExt("signing.password"),
) = extensions.configure<SigningExtension> {
    useInMemoryPgpKeys(keyId, key, password)
    sign(extensions.getByType<PublishingExtension>().publications)
}

fun Project.defaultPublishing(lib: LibDetails, readmeFile: File = File(rootDir, "README.md")) {

    val readmeJavadocJar by tasks.registering(Jar::class) {
        from(readmeFile) // TODO_maybe: use dokka to create real docs? (but it's not even java..)
        archiveClassifier put "javadoc"
    }

    extensions.configure<PublishingExtension> {
        publications.withType<MavenPublication> {
            artifact(readmeJavadocJar)
            // Adding javadoc artifact generates warnings like:
            // Execution optimizations have been disabled for task ':uspek:signJvmPublication'
            // It looks like a bug in kotlin multiplatform plugin:
            // https://youtrack.jetbrains.com/issue/KT-46466
            // FIXME_someday: Watch the issue.
            // If it's a bug in kotlin multiplatform then remove this comment when it's fixed.
            // Some related bug reports:
            // https://youtrack.jetbrains.com/issue/KT-47936
            // https://github.com/gradle/gradle/issues/17043

            defaultPOM(lib)
        }
    }
}


// endregion [Kotlin Module Build Template]

// region [Jvm App Build Template]

@Suppress("UNUSED_VARIABLE")
fun Project.defaultBuildTemplateForJvmApp(
    appMainPackage: String,
    appMainClass: String = "MainKt",
    details: LibDetails = libs.Unknown,
    withTestJUnit4: Boolean = false,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    repositories { defaultRepos() }
    defaultGroupAndVerAndDescription(details)

    kotlin {
        sourceSets {
            val main by getting {
                dependencies {
                    addMainDependencies()
                }
            }
            val test by getting {
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
    }

    application { mainClass put "$appMainPackage.$appMainClass" }

    tasks.defaultKotlinCompileOptions()
    tasks.defaultTestsOptions(onJvmUseJUnitPlatform = withTestJUnit5)
}

// endregion [Jvm App Build Template]