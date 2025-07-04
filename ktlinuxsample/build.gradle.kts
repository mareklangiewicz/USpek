
// region [[Basic MPP App Build Imports and Plugs]]

import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import com.vanniktech.maven.publish.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

plugins {
  plugAll(plugs.KotlinMulti, plugs.VannikPublish)
}

// endregion [[Basic MPP App Build Imports and Plugs]]

val details = myLibDetails(
  name = "ktsample",
  settings = LibSettings(
    withJvm = false,
    withJs = false,
    withNativeLinux64 = true,
    compose = null,
    withTestJUnit4 = false,
    withTestJUnit5 = false,
    withTestUSpekX = false, // FIXME: temporarily defined by hand below (to :project directly)
  ),
)

defaultBuildTemplateForBasicMppApp(details) {
  implementation(project(":uspekx"))
  implementation(Langiewicz.kground)
  // https://s01.oss.sonatype.org/content/repositories/releases/pl/mareklangiewicz/kground/
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
  @Suppress("DEPRECATION")
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
  apiVer: KotlinVersion = KotlinVersion.KOTLIN_2_1,
  jvmTargetVer: String? = null, // it's better to use jvmToolchain (normally done in fun allDefault)
  renderInternalDiagnosticNames: Boolean = false,
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions {
    apiVersion.set(apiVer)
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
fun MavenPom.defaultPOM(lib: LibDetails) {
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

fun Project.defaultPublishing(lib: LibDetails) = extensions.configure<MavenPublishBaseExtension> {
  propertiesTryOverride("signingInMemoryKey", "signingInMemoryKeyPassword", "mavenCentralPassword")
  if (lib.settings.withCentralPublish) publishToMavenCentral(automaticRelease = false)
  signAllPublications()
  signAllPublicationsFixSignatoryIfFound()
  // Note: artifactId is not lib.name but current project.name (module name)
  coordinates(groupId = lib.group, artifactId = name, version = lib.version.str)
  pom { defaultPOM(lib) }
}

// endregion [[Kotlin Module Build Template]]

// region [[MPP Module Build Template]]

/**
 * Only for very standard small libs. In most cases it's better to not use this function.
 *
 * These ignoreXXX flags are hacky, but needed. see [allDefault] kdoc for details.
 */
fun Project.defaultBuildTemplateForBasicMppLib(
  details: LibDetails = rootExtLibDetails,
  ignoreCompose: Boolean = false, // so user have to explicitly say THAT he wants to ignore compose settings here.
  ignoreAndroTarget: Boolean = false, // so user have to explicitly say IF he wants to ignore it.
  ignoreAndroConfig: Boolean = false, // so user have to explicitly say THAT he wants to ignore it.
  ignoreAndroPublish: Boolean = false, // so user have to explicitly say THAT he wants to ignore it.
  addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
) {
  require(ignoreCompose || details.settings.compose == null) { "defaultBuildTemplateForBasicMppLib can not configure compose stuff" }
  details.settings.andro?.let {
    require(ignoreAndroConfig) { "defaultBuildTemplateForBasicMppLib can not configure android stuff (besides just adding target)" }
    require(ignoreAndroPublish || it.publishNoVariants) { "defaultBuildTemplateForBasicMppLib can not publish android stuff YET" }
  }
  repositories { addRepos(details.settings.repos) }
  defaultGroupAndVerAndDescription(details)
  extensions.configure<KotlinMultiplatformExtension> {
    allDefault(
      settings = details.settings,
      ignoreCompose = ignoreCompose,
      ignoreAndroTarget = ignoreAndroTarget,
      ignoreAndroConfig = ignoreAndroConfig,
      ignoreAndroPublish = ignoreAndroPublish,
      addCommonMainDependencies = addCommonMainDependencies,
    )
  }
  configurations.checkVerSync(warnOnly = true)
  tasks.defaultKotlinCompileOptions(jvmTargetVer = null) // jvmVer is set in fun allDefault using jvmToolchain
  tasks.defaultTestsOptions(onJvmUseJUnitPlatform = details.settings.withTestJUnit5)
  if (plugins.hasPlugin("com.vanniktech.maven.publish")) defaultPublishing(details)
  else println("MPP Module ${name}: publishing (and signing) disabled")
}

/**
 * Only for very standard small libs. In most cases it's better to not use this function.
 *
 * These ignoreXXX flags are hacky, but needed because we want to inject this code also to such build files,
 * where plugins for compose and/or android are not applied at all, so compose/android stuff should be explicitly ignored,
 * and then configured right after this call, using code from another special region (region using compose and/or andro plugin stuff).
 * Also kmp andro publishing is in the middle of big changes, so let's not support it yet, and let's wait for more clarity regarding:
 * https://youtrack.jetbrains.com/issue/KT-61575/Publishing-a-KMP-library-handles-Android-target-inconsistently-requiring-an-explicit-publishLibraryVariants-call-to-publish
 * https://youtrack.jetbrains.com/issue/KT-60623/Deprecate-publishAllLibraryVariants-in-kotlin-android
 */
fun KotlinMultiplatformExtension.allDefault(
  settings: LibSettings,
  ignoreCompose: Boolean = false, // so user have to explicitly say THAT he wants to ignore compose settings here.
  ignoreAndroTarget: Boolean = false, // so user have to explicitly say IF he wants to ignore it.
  ignoreAndroConfig: Boolean = false, // so user have to explicitly say THAT he wants to ignore it.
  ignoreAndroPublish: Boolean = false, // so user have to explicitly say THAT he wants to ignore it.
  addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
) = with(settings) {
  require(ignoreCompose || compose == null) { "allDefault can not configure compose stuff" }
  andro?.let {
    require(ignoreAndroConfig) { "allDefault can not configure android stuff (besides just adding target)" }
    require(ignoreAndroPublish || it.publishNoVariants) { "allDefault can not publish android stuff YET" }
  }
  if (withJvm) jvm()
  if (withJs) jsDefault()
  if (withNativeLinux64) linuxX64()
  if (withAndro && !ignoreAndroTarget) androidTarget {
    // TODO_someday some kmp andro publishing. See kdoc above why not yet.
  }
  withJvmVer?.let { jvmToolchain(it.toInt()) } // works for jvm and android
  sourceSets {
    val commonMain by getting {
      dependencies {
        if (withKotlinxHtml) implementation(KotlinX.html)
        addCommonMainDependencies()
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(Kotlin.test)
        if (withTestUSpekX) implementation(Langiewicz.uspekx)
      }
    }
    if (withJvm) {
      val jvmTest by getting {
        dependencies {
          if (withTestJUnit4) implementation(JUnit.junit)
          if (withTestJUnit5) {
            implementation(Org.JUnit.Jupiter.junit_jupiter_engine)
            runtimeOnly(Org.JUnit.Platform.junit_platform_launcher)
          }
          if (withTestUSpekX) {
            implementation(Langiewicz.uspekx)
            if (withTestJUnit4) implementation(Langiewicz.uspekx_junit4)
            if (withTestJUnit5) implementation(Langiewicz.uspekx_junit5)
          }
          if (withTestGoogleTruth) implementation(Com.Google.Truth.truth)
          if (withTestMockitoKotlin) implementation(Org.Mockito.Kotlin.mockito_kotlin)
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

// endregion [[MPP Module Build Template]]

// region [[MPP App Build Template]]

fun Project.defaultBuildTemplateForBasicMppApp(
  details: LibDetails = rootExtLibDetails,
  ignoreCompose: Boolean = false, // so user have to explicitly say THAT he wants to ignore compose settings here.
  ignoreAndroTarget: Boolean = false, // so user have to explicitly say IF he wants to ignore it.
  ignoreAndroConfig: Boolean = false, // so user have to explicitly say THAT he wants to ignore it.
  addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
) {
  defaultBuildTemplateForBasicMppLib(
    details = details,
    ignoreCompose = ignoreCompose,
    ignoreAndroTarget = ignoreAndroTarget,
    ignoreAndroConfig = ignoreAndroConfig,
    ignoreAndroPublish = true,
    addCommonMainDependencies = addCommonMainDependencies,
  )
  extensions.configure<KotlinMultiplatformExtension> {
    if (details.settings.withJvm) jvm {
      mainRun {
        mainClass = details.run { "$appMainPackage.$appMainClass" }
        logger.info("MPP App ${project.name}: MPP plugin (without compose) just adds jvmRun task (experimental). No executable.")
        // Workaround to generate jvm binary: Separate jvm module with plugAll(plugs.KotlinJvm, plugs.JvmApp)
        // As Gradle/KMP warning says (when trying to combine KotlinMulti with JvmApp):
        // w: 'application' (also applies 'java' plugin) Gradle plugin is not compatible with 'org.jetbrains.kotlin.multiplatform' plugin.
        // Consider adding a new subproject with 'application' plugin where the KMP project is added as a dependency.
        // see also:
        // https://youtrack.jetbrains.com/issue/KT-45038
        // https://youtrack.jetbrains.com/issue/KT-31424
      }
    }
    if (details.settings.withJs) js(IR) {
      binaries.executable()
    }
    if (details.settings.withNativeLinux64) linuxX64 {
      binaries {
        executable {
          entryPoint = details.run { "$appMainPackage.$appMainFun" }
        }
      }
    }
  }
}

// endregion [[MPP App Build Template]]
