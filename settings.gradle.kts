rootProject.name = "USpek"


// Careful with auto publishing fails/stack traces
val buildScanPublishingAllowed =
  System.getenv("GITHUB_ACTIONS") == "true"
  // true
  // false

// region [[My Settings Stuff <~~]]
// ~~>".*/Deps\.kt"~~>"../DepsKt"<~~
// endregion [[My Settings Stuff <~~]]
// region [[My Settings Stuff]]

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }

  val depsDir = File(rootDir, "../DepsKt").normalize()
  val depsInclude =
    // depsDir.exists()
    false
  if (depsInclude) {
    logger.warn("Including local build $depsDir")
    includeBuild(depsDir)
  }
}

plugins {
  id("pl.mareklangiewicz.deps.settings") version "0.3.87" // https://plugins.gradle.org/search?term=mareklangiewicz
  id("com.gradle.develocity") version "4.0.2" // https://docs.gradle.com/develocity/gradle-plugin/
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
    publishing.onlyIf { buildScanPublishingAllowed && it.buildResult.failures.isNotEmpty() }
  }
}

// endregion [[My Settings Stuff]]

// Note: it may be good idea to comment out / disable some subprojects (like ktandrosample) to save memory/build time

include(
  ":uspek",
  ":uspekx",
  ":uspekx-junit4",
  ":uspekx-junit5",

  ":ktjsreactsample",

  ":ktjunit4sample",
  ":ktjunit5sample",
  ":ktmultisample",
  ":ktlinuxsample",
  // ":ktandrosample",
)


/*

FIXME_later: Releasing USpek with :ktandrosample enabled failed.
Looks like bug with tasks dependencies related to resources.
Don't know if't on android side or compose mpp side
(compose mpp is changing resource management recently)
Unfortunately the bug did not reproduce on my machine,
and generally looks like an issue which can happen very randomly
(race conditions between tasks?)

report from github:
https://github.com/mareklangiewicz/USpek/actions/runs/10130733354/job/28012490261
https://scans.gradle.com/s/qtvw3gn3xdqt2

*/
