import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

plugins {
    id("com.android.library")
    kotlin("android")
    // id("maven-publish")
    // id("signing")
}

defaultBuildTemplateForAndroidLib(
    libNamespace = "pl.mareklangiewicz.ktandrosample",
    withCompose = true,
    withComposeCompilerVer = vers.composeCompilerEdge,
)

android {
    dependencies {
        // TODO_later: Analyze why do I have to repeat it with "androidTestImplementation" configuration.
        // I already added it in with "testImplementation" configuration (in defaultBuildTemplateForAndroidLib)
        // Isn't testImplementation always included in androidTestImplementation?? I thought it is.
        defaultAndroTestDeps("androidTestImplementation", withCompose = true)
    }
}

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
    renderInternalDiagnosticNames: Boolean = false,
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = jvmTargetVer
        if (renderInternalDiagnosticNames) freeCompilerArgs = freeCompilerArgs + "-Xrender-internal-diagnostic-names"
        // useful for example to suppress some errors when accessing internal code from some library, like:
        // @file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE", "EXPOSED_PROPERTY_TYPE", "CANNOT_OVERRIDE_INVISIBLE_MEMBER")
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
    key: String = rootExtReadFileUtf8("signing.keyFile"),
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

// region [Andro Common Build Template]


/** usually not needed - see template-andro */
fun ScriptHandlerScope.defaultAndroBuildScript() {
    repositories {
        defaultRepos(withGradle = true)
    }
    dependencies {
        defaultAndroBuildScriptDeps()
    }
}


/** usually not needed - see template-android */
fun DependencyHandler.defaultAndroBuildScriptDeps(
) {
    add("classpath", deps.kotlinGradlePlugin)
    add("classpath", deps.androidGradlePlugin)
}


fun DependencyHandler.defaultAndroDeps(
    configuration: String = "implementation",
    withCompose: Boolean = false,
) = deps.run {
    addAll(
        configuration,
        androidxCoreKtx,
        androidxAppcompat,
        androidMaterial,
        androidxLifecycleCompiler,
        androidxLifecycleRuntimeKtx,
    )
    if (withCompose) addAll(
        configuration,
        composeAndroidUi,
        composeAndroidUiTooling,
        composeAndroidUiToolingPreview,
        composeAndroidMaterial3,
        composeAndroidMaterial,
        androidxActivityCompose,
    )
}

fun DependencyHandler.defaultAndroTestDeps(
    configuration: String = "testImplementation",
    withCompose: Boolean = false,
) = deps.run {
    addAll(
        configuration,
        kotlinTestJUnit,
        junit4, // FIXME_someday: when will android move to JUnit5?
        uspekxJUnit4,
        androidxEspressoCore,
        googleTruth,
        androidxTestRules,
        androidxTestRunner,
        androidxTestExtTruth,
        androidxTestExtJUnit,
        mockitoKotlin4,
    )
    if (withCompose) addAll(
        configuration,
        composeAndroidUiTest,
        composeAndroidUiTestJUnit4,
        composeAndroidUiTestManifest,
    )
}

fun MutableSet<String>.defaultAndroExcludedResources() = addAll(
    listOf(
        "**/*.md",
        "**/attach_hotspot_windows.dll",
        "META-INF/licenses/**",
        "META-INF/AL2.0",
        "META-INF/LGPL2.1",
    )
)

fun CommonExtension<*, *, *, *>.defaultCompileOptions(
    jvmVersion: String = vers.defaultJvm,
) = compileOptions {
    sourceCompatibility(jvmVersion)
    targetCompatibility(jvmVersion)
}

fun CommonExtension<*, *, *, *>.defaultComposeStuff(withComposeCompilerVer: String? = null) {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = withComposeCompilerVer
    }
}

fun CommonExtension<*, *, *, *>.defaultPackagingOptions() = packagingOptions {
    resources.excludes.defaultAndroExcludedResources()
}

/** Use template-andro/build.gradle.kts:fun defaultAndroLibPublishAllVariants() to create component with name "default". */
fun Project.defaultPublishingOfAndroLib(
    lib: LibDetails,
    componentName: String = "default",
) {
    afterEvaluate {
        extensions.configure<PublishingExtension> {
            publications.register<MavenPublication>(componentName) {
                from(components[componentName])
                defaultPOM(lib)
            }
        }
    }
}

fun Project.defaultPublishingOfAndroApp(
    lib: LibDetails,
    componentName: String = "release",
) = defaultPublishingOfAndroLib(lib, componentName)


// endregion [Andro Common Build Template]

// region [Andro Lib Build Template]

fun Project.defaultBuildTemplateForAndroidLib(
    libNamespace: String,
    jvmVersion: String = vers.defaultJvm,
    sdkCompile: Int = vers.androidSdkCompile,
    sdkTarget: Int = vers.androidSdkTarget,
    sdkMin: Int = vers.androidSdkMin,
    withCompose: Boolean = false,
    withComposeCompilerVer: String? = null,
    details: LibDetails = libs.Unknown,
    publishVariant: String? = null, // null means disable publishing to maven repo
) {
    repositories { defaultRepos(withComposeCompilerAndroidxDev = withCompose) }
    android {
        defaultAndroLib(libNamespace, jvmVersion, sdkCompile, sdkTarget, sdkMin, withCompose, withComposeCompilerVer)
        publishVariant?.let { defaultAndroLibPublishVariant(it) }
    }
    dependencies {
        defaultAndroDeps(withCompose = withCompose)
        defaultAndroTestDeps(withCompose = withCompose)
    }
    tasks.defaultKotlinCompileOptions()
    defaultGroupAndVerAndDescription(details)
    publishVariant?.let {
        defaultPublishingOfAndroLib(details, it)
        defaultSigning()
    }
}

fun LibraryExtension.defaultAndroLib(
    libNamespace: String,
    jvmVersion: String = vers.defaultJvm,
    sdkCompile: Int = vers.androidSdkCompile,
    sdkTarget: Int = vers.androidSdkTarget,
    sdkMin: Int = vers.androidSdkMin,
    withCompose: Boolean = false,
    withComposeCompilerVer: String? = null,
) {
    compileSdk = sdkCompile
    defaultCompileOptions(jvmVersion)
    defaultDefaultConfig(libNamespace, sdkTarget, sdkMin)
    defaultBuildTypes()
    if (withCompose) defaultComposeStuff(withComposeCompilerVer)
    defaultPackagingOptions()
}

fun LibraryExtension.defaultDefaultConfig(
    libNamespace: String,
    sdkTarget: Int = vers.androidSdkTarget,
    sdkMin: Int = vers.androidSdkMin,
) = defaultConfig {
    namespace = libNamespace
    targetSdk = sdkTarget
    minSdk = sdkMin
    testInstrumentationRunner = vers.androidTestRunnerClass
}

fun LibraryExtension.defaultBuildTypes() = buildTypes { release { isMinifyEnabled = false } }

fun LibraryExtension.defaultAndroLibPublishVariant(
    variant: String = "debug",
    withSources: Boolean = true,
    withJavadoc: Boolean = false,
) {
    publishing {
        singleVariant(variant) {
            if (withSources) withSourcesJar()
            if (withJavadoc) withJavadocJar()
        }
    }
}

fun LibraryExtension.defaultAndroLibPublishAllVariants(
    withSources: Boolean = true,
    withJavadoc: Boolean = false,
) {
    publishing {
        multipleVariants {
            allVariants()
            if (withSources) withSourcesJar()
            if (withJavadoc) withJavadocJar()
        }
    }
}

// endregion [Andro Lib Build Template]