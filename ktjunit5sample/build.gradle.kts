
// region [[Basic JVM App Build Imports and Plugs]]

import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import com.vanniktech.maven.publish.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

plugins {
  plugAll(
    plugs.KotlinJvm,
    plugs.JvmApp,
    plugs.VannikPublish,
  )
}

// endregion [[Basic JVM App Build Imports and Plugs]]

val details = myLibDetails(
  name = "ktsample",
  settings = LibSettings(
    withJs = false,
    compose = null,
    withTestJUnit4 = false,
    withTestJUnit5 = true,
    withTestUSpekX = false, // FIXME: temporarily defined by hand below (to :project directly)
  ),
)

defaultBuildTemplateForBasicJvmApp(details) {
  implementation(project(":uspekx-junit5"))
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

// region [[JVM Module Build Template]]

/**
 * Only for very standard small jvm libs. In most cases it's better to not use this function.
 *
 * These ignoreXXX flags are hacky, but needed. see [jvmOnlyDefault] kdoc for details.
 */
fun Project.defaultBuildTemplateForBasicJvmLib(
  details: LibDetails = rootExtLibDetails,
  ignoreCompose: Boolean = false, // so user have to explicitly say THAT he wants to ignore compose settings here.
  ignoreAndroTarget: Boolean = false, // so user have to explicitly say THAT he wants to ignore android target.
  addJvmDependencies: DependencyHandlerScope.() -> Unit = {},
) {
  require(ignoreCompose || details.settings.compose == null) { "defaultBuildTemplateForBasicJvmLib can NOT configure compose stuff" }
  require(ignoreAndroTarget || details.settings.andro == null) { "defaultBuildTemplateForBasicJvmLib can NOT configure android target" }
  repositories { addRepos(details.settings.repos) }
  defaultGroupAndVerAndDescription(details)
  extensions.configure<KotlinJvmProjectExtension> {
    jvmOnlyDefault(
      settings = details.settings,
      ignoreCompose = ignoreCompose,
      ignoreAndroTarget = ignoreAndroTarget,
      addJvmDependencies = addJvmDependencies,
    )
  }
  configurations.checkVerSync(warnOnly = true)
  tasks.defaultKotlinCompileOptions(jvmTargetVer = null) // jvmVer is set in fun jvmDefault using jvmToolchain
  tasks.defaultTestsOptions(onJvmUseJUnitPlatform = details.settings.withTestJUnit5)
  if (plugins.hasPlugin("com.vanniktech.maven.publish")) defaultPublishing(details)
  else println("JVM Module ${name}: publishing (and signing) disabled")
}

/**
 * Only for very standard small jvm libs. In most cases it's better to not use this function.
 *
 * These ignoreXXX flags are hacky, but needed because we want to inject this code to such build files,
 * where plugins for compose and/or android are not applied at all, so compose/android stuff should be explicitly ignored.
 */
fun KotlinJvmProjectExtension.jvmOnlyDefault(
  settings: LibSettings,
  ignoreCompose: Boolean = false, // so user have to explicitly say THAT he wants to ignore compose settings here.
  ignoreAndroTarget: Boolean = false, // so user have to explicitly say THAT he wants to ignore it.
  addJvmDependencies: DependencyHandlerScope.() -> Unit = {},
) = with(settings) {
  require(ignoreCompose || compose == null) { "jvmOnlyDefault can NOT configure compose stuff" }
  require(ignoreAndroTarget || settings.andro == null) { "jvmOnlyDefault can NOT configure android target" }
  withJvmVer?.let { jvmToolchain(it.toInt()) } // works for jvm and android
  dependencies {
    if (withKotlinxHtml) implementation(KotlinX.html)
    testImplementation(Kotlin.test)
    if (withTestUSpekX) testImplementation(Langiewicz.uspekx)
    if (withTestJUnit4) testImplementation(JUnit.junit)
    if (withTestJUnit5) {
      testImplementation(Org.JUnit.Jupiter.junit_jupiter_engine)
      testRuntimeOnly(Org.JUnit.Platform.junit_platform_launcher)
    }
    if (withTestUSpekX) {
      testImplementation(Langiewicz.uspekx)
      if (withTestJUnit4) testImplementation(Langiewicz.uspekx_junit4)
      if (withTestJUnit5) testImplementation(Langiewicz.uspekx_junit5)
    }
    if (withTestGoogleTruth) testImplementation(Com.Google.Truth.truth)
    if (withTestMockitoKotlin) testImplementation(Org.Mockito.Kotlin.mockito_kotlin)
    addJvmDependencies()
  }
}

// endregion [[JVM Module Build Template]]

// region [[JVM App Build Template]]

fun Project.defaultBuildTemplateForBasicJvmApp(
  details: LibDetails = rootExtLibDetails,
  ignoreCompose: Boolean = false, // so user have to explicitly say THAT he wants to ignore compose settings here.
  ignoreAndroTarget: Boolean = false, // so user have to explicitly say THAT he wants to ignore android target.
  addJvmDependencies: DependencyHandlerScope.() -> Unit = {},
) {
  defaultBuildTemplateForBasicJvmLib(details, ignoreCompose, ignoreAndroTarget, addJvmDependencies)
  extensions.configure<JavaApplication> {
    mainClass = details.run { "$appMainPackage.$appMainClass" }
  }
}

// endregion [[JVM App Build Template]]
