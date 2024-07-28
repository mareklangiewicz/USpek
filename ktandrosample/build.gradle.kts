// region [Custom Andro Lib Build Imports and Plugs]

import com.android.build.api.dsl.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

plugins {
  plugAll(
    plugs.KotlinMulti,
    plugs.KotlinMultiCompose,
    plugs.ComposeJb,
    plugs.AndroLib,
  )
}

// endregion [Custom Andro Lib Build Imports and Plugs]

val details = myLibDetails(
  name = "ktsample",
  description = "USpek Andro Lib Sample.",
  githubUrl = "https://github.com/mareklangiewicz/USpek",
  settings = LibSettings(
    withJs = false,
    withTestJUnit4 = true,
    withTestJUnit5 = false,
    withTestUSpekX = true, // We explicitly pull published version of USpekX (not local :project..)
    andro = LibAndroSettings(
      sdkCompilePreview = Vers.AndroSdkPreview,
      // publishVariant = "debug",
    ),
  ),
)

defaultBuildTemplateForAndroLib(details)

dependencies {
  defaultAndroTestDeps(details.settings, configuration = "androidTestImplementation")
  // TODO_someday: investigate why "androidTestImplementation" doesn't inherit from "testImplementation"
}

// region [[Kotlin Module Build Template]]

// Kind of experimental/temporary.. not sure how it will evolve yet,
// but currently I need these kind of substitutions/locals often enough
// especially when updating kground <-> kommandline (trans deps issues)
fun Project.setMyWeirdSubstitutions(
  vararg rules: Pair<String, String>,
  myProjectsGroup: String = "pl.mareklangiewicz",
  tryToUseLocalProjects: Boolean = true,
) {
  val foundLocalProjects: Map<String, Project?> =
    if (tryToUseLocalProjects) rules.associate { it.first to findProject(":${it.first}") }
    else emptyMap()
  configurations.all {
    resolutionStrategy.dependencySubstitution {
      for ((projName, projVer) in rules)
        substitute(module("$myProjectsGroup:$projName"))
          .using(
            // Note: there are different fun in gradle: Project.project; DependencySubstitution.project
            if (foundLocalProjects[projName] != null) project(":$projName")
            else module("$myProjectsGroup:$projName:$projVer")
          )
    }
  }
}

fun RepositoryHandler.addRepos(settings: LibReposSettings) = with(settings) {
  if (withMavenLocal) mavenLocal()
  if (withMavenCentral) mavenCentral()
  if (withGradle) gradlePluginPortal()
  if (withGoogle) google()
  if (withKotlinx) maven(repos.kotlinx)
  if (withKotlinxHtml) maven(repos.kotlinxHtml)
  if (withComposeJbDev) maven(repos.composeJbDev)
  if (withKtorEap) maven(repos.ktorEap)
  if (withJitpack) maven(repos.jitpack)
}

// TODO_maybe: doc says it could be now also applied globally instead for each task (and it works for andro too)
//   But it's only for jvm+andro, so probably this is better:
//   https://kotlinlang.org/docs/gradle-compiler-options.html#for-all-kotlin-compilation-tasks
fun TaskCollection<Task>.defaultKotlinCompileOptions(
  jvmTargetVer: String? = null, // it's better to use jvmToolchain (normally done in fun allDefault)
  renderInternalDiagnosticNames: Boolean = false,
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions {
    apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0) // FIXME_later: add param.
    jvmTargetVer?.let { jvmTarget = JvmTarget.fromTarget(it) }
    if (renderInternalDiagnosticNames) freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
    // useful, for example, to suppress some errors when accessing internal code from some library, like:
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

/** See also: root project template-full: addDefaultStuffFromSystemEnvs */
fun Project.defaultSigning(
  keyId: String = rootExtString["signing.keyId"],
  key: String = rootExtReadFileUtf8TryOrNull("signing.keyFile") ?: rootExtString["signing.key"],
  password: String = rootExtString["signing.password"],
) = extensions.configure<SigningExtension> {
  useInMemoryPgpKeys(keyId, key, password)
  sign(extensions.getByType<PublishingExtension>().publications)
}

fun Project.defaultPublishing(
  lib: LibDetails,
  readmeFile: File = File(rootDir, "README.md"),
  withSignErrorWorkaround: Boolean = true,
  withPublishingPrintln: Boolean = false, // FIXME_later: enabling brakes gradle android publications
) {

  val readmeJavadocJar by tasks.registering(Jar::class) {
    from(readmeFile) // TODO_maybe: use dokka to create real docs? (but it's not even java..)
    archiveClassifier put "javadoc"
  }

  extensions.configure<PublishingExtension> {

    // We have at least two cases:
    // 1. With plug.KotlinMulti it creates publications automatically (so no need to create here)
    // 2. With plug.KotlinJvm it does not create publications (so we have to create it manually)
    if (plugins.hasPlugin("org.jetbrains.kotlin.jvm")) {
      publications.create<MavenPublication>("jvm") {
        from(components["kotlin"])
      }
    }

    publications.withType<MavenPublication> {
      artifact(readmeJavadocJar)
      // Adding javadoc artifact generates warnings like:
      // Execution optimizations have been disabled for task ':uspek:signJvmPublication'
      // (UPDATE: now it's errors - see workaround below)
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
  if (withSignErrorWorkaround) tasks.withSignErrorWorkaround() // very much related to comments above too
  if (withPublishingPrintln) tasks.withPublishingPrintln()
}

/*
Hacky workaround for gradle error with signing+publishing on gradle 8.1-rc-1:

FAILURE: Build failed with an exception.

* What went wrong:
A problem was found with the configuration of task ':template-full-lib:signJvmPublication' (type 'Sign').
  - Gradle detected a problem with the following location: '/home/marek/code/kotlin/KGround/template-full/template-full-lib/build/libs/template-full-lib-0.0.02-javadoc.jar.asc'.

    Reason: Task ':template-full-lib:publishJsPublicationToMavenLocal' uses this output of task ':template-full-lib:signJvmPublication' without declaring an explicit or implicit dependency. This can lead to incorrect results being produced, depending on what order the tasks are executed.

    Possible solutions:
      1. Declare task ':template-full-lib:signJvmPublication' as an input of ':template-full-lib:publishJsPublicationToMavenLocal'.
      2. Declare an explicit dependency on ':template-full-lib:signJvmPublication' from ':template-full-lib:publishJsPublicationToMavenLocal' using Task#dependsOn.
      3. Declare an explicit dependency on ':template-full-lib:signJvmPublication' from ':template-full-lib:publishJsPublicationToMavenLocal' using Task#mustRunAfter.

    Please refer to https://docs.gradle.org/8.1-rc-1/userguide/validation_problems.html#implicit_dependency for more details about this problem.

 */
fun TaskContainer.withSignErrorWorkaround() =
  withType<AbstractPublishToMaven>().configureEach { dependsOn(withType<Sign>()) }

fun TaskContainer.withPublishingPrintln() = withType<AbstractPublishToMaven>().configureEach {
  val coordinates = publication.run { "$groupId:$artifactId:$version" }
  when (this) {
    is PublishToMavenRepository -> doFirst {
      println("Publishing $coordinates to ${repository.url}")
    }
    is PublishToMavenLocal -> doFirst {
      val localRepo = System.getenv("HOME")!! + "/.m2/repository"
      val localPath = localRepo + publication.run { "/$groupId/$artifactId".replace('.', '/') }
      println("Publishing $coordinates to $localPath")
    }
  }
}

// endregion [[Kotlin Module Build Template]]

// region [[Andro Common Build Template]]

/** @param ignoreCompose Should be set to true if compose mpp is configured instead of compose andro */
fun DependencyHandler.defaultAndroDeps(
  settings: LibSettings,
  ignoreCompose: Boolean = false,
  configuration: String = "implementation",
) {
  val andro = settings.andro ?: error("No andro settings.")
  addAll(
    configuration,
    AndroidX.Core.ktx,
    AndroidX.AppCompat.appcompat.takeIf { andro.withAppCompat },
    AndroidX.Activity.compose.takeIf { andro.withActivityCompose }, // this should not depend on ignoreCompose!
    AndroidX.Lifecycle.compiler.takeIf { andro.withLifecycle },
    AndroidX.Lifecycle.runtime_ktx.takeIf { andro.withLifecycle },
    // TODO_someday_maybe: more lifecycle related stuff by default (viewmodel, compose)?
    Com.Google.Android.Material.material.takeIf { andro.withMDC },
  )
  if (!ignoreCompose && settings.withCompose) {
    val compose = settings.compose!!
    addAllWithVer(
      configuration,
      Vers.ComposeAndro,
      AndroidX.Compose.Ui.ui,
      AndroidX.Compose.Ui.tooling,
      AndroidX.Compose.Ui.tooling_preview,
      AndroidX.Compose.Material.material.takeIf { compose.withComposeMaterial2 },
    )
    addAll(
      configuration,
      AndroidX.Compose.Material3.material3.takeIf { compose.withComposeMaterial3 },
    )
  }
}

/** @param ignoreCompose Should be set to true if compose mpp is configured instead of compose andro */
fun DependencyHandler.defaultAndroTestDeps(
  settings: LibSettings,
  ignoreCompose: Boolean = false,
  configuration: String = "testImplementation",
) {
  val andro = settings.andro ?: error("No andro settings.")
  addAll(
    configuration,
    AndroidX.Test.Espresso.core.takeIf { andro.withTestEspresso },
    Com.Google.Truth.truth.takeIf { settings.withTestGoogleTruth },
    AndroidX.Test.rules,
    AndroidX.Test.runner,
    AndroidX.Test.Ext.truth.takeIf { settings.withTestGoogleTruth },
    Org.Mockito.Kotlin.mockito_kotlin.takeIf { settings.withTestMockitoKotlin },
  )

  if (settings.withTestJUnit4) {
    addAll(
      configuration,
      Kotlin.test_junit.withVer(Vers.Kotlin),
      JUnit.junit,
      Langiewicz.uspekx_junit4.takeIf { settings.withTestUSpekX },
      AndroidX.Test.Ext.junit_ktx,
    )
  }
  // android doesn't fully support JUnit5, but adding deps anyway to be able to write JUnit5 dependent code
  if (settings.withTestJUnit5) {
    addAll(
      configuration,
      Kotlin.test_junit5.withVer(Vers.Kotlin),
      Org.JUnit.Jupiter.junit_jupiter_api,
      Org.JUnit.Jupiter.junit_jupiter_engine,
      Langiewicz.uspekx_junit5.takeIf { settings.withTestUSpekX },
    )
  }

  if (!ignoreCompose && settings.withCompose) addAllWithVer(
    configuration,
    vers.ComposeAndro,
    AndroidX.Compose.Ui.test,
    AndroidX.Compose.Ui.test_manifest,
    AndroidX.Compose.Ui.test_junit4.takeIf { settings.withTestJUnit4 },
  )
}

fun MutableSet<String>.defaultAndroExcludedResources() = addAll(
  listOf(
    "**/*.md",
    "**/attach_hotspot_windows.dll",
    "META-INF/licenses/**",
    "META-INF/AL2.0",
    "META-INF/LGPL2.1",
    "META-INF/kotlinx_coroutines_core.version",
  ),
)

fun CommonExtension<*, *, *, *, *, *>.defaultCompileOptions(
  jvmVer: String? = null, // it's better to use jvmToolchain (normally done in fun allDefault)
) = compileOptions {
  jvmVer?.let {
    sourceCompatibility(it)
    targetCompatibility(it)
  }
}

fun CommonExtension<*, *, *, *, *, *>.defaultComposeStuff() {
  buildFeatures {
    compose = true
  }
}

fun CommonExtension<*, *, *, *, *, *>.defaultPackagingOptions() = packaging {
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


// endregion [[Andro Common Build Template]]

// region [[Andro Lib Build Template]]

fun Project.defaultBuildTemplateForAndroLib(
  details: LibDetails = rootExtLibDetails,
  addAndroMainDependencies: DependencyHandler.() -> Unit = {},
) {
  val andro = details.settings.andro ?: error("No andro settings.")
  repositories { addRepos(details.settings.repos) }
  extensions.configure<KotlinMultiplatformExtension> {
    androidTarget()
    details.settings.withJvmVer?.let { jvmToolchain(it.toInt()) } // works for jvm and android
  }
  extensions.configure<LibraryExtension> {
    defaultAndroLib(details)
    if (andro.publishAllVariants) defaultAndroLibPublishAllVariants()
    if (andro.publishOneVariant) defaultAndroLibPublishVariant(andro.publishVariant)
  }
  dependencies {
    defaultAndroDeps(details.settings)
    defaultAndroTestDeps(details.settings)
    add("debugImplementation", AndroidX.Tracing.ktx) // https://github.com/android/android-test/issues/1755
    addAndroMainDependencies()
  }
  configurations.checkVerSync(warnOnly = true)
  tasks.defaultKotlinCompileOptions(
    jvmTargetVer = null, // jvmVer is set jvmToolchain in fun allDefault
  )
  defaultGroupAndVerAndDescription(details)
  if (andro.publishAllVariants) defaultPublishingOfAndroLib(details, "default")
  if (andro.publishOneVariant) defaultPublishingOfAndroLib(details, andro.publishVariant)
  if (!andro.publishNoVariants) defaultSigning()
}

fun LibraryExtension.defaultAndroLib(
  details: LibDetails = rootExtLibDetails,
  ignoreCompose: Boolean = false,
) {
  val andro = details.settings.andro ?: error("No andro settings.")
  andro.sdkCompilePreview?.let { compileSdkPreview = it } ?: run { compileSdk = andro.sdkCompile }
  defaultCompileOptions(jvmVer = null) // actually it does nothing now. jvm ver is normally configured via jvmToolchain
  defaultDefaultConfig(details)
  defaultBuildTypes()
  details.settings.compose?.takeIf { !ignoreCompose }?.let { defaultComposeStuff() }
  defaultPackagingOptions()
}

fun LibraryExtension.defaultDefaultConfig(details: LibDetails) = defaultConfig {
  val asettings = details.settings.andro ?: error("No andro settings.")
  namespace = details.namespace
  minSdk = asettings.sdkMin
  testInstrumentationRunner = asettings.withTestRunner
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

// endregion [[Andro Lib Build Template]]
