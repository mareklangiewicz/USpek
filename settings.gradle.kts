@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.extLib

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
  id("pl.mareklangiewicz.deps.settings") version "0.4.62" // https://plugins.gradle.org/search?term=mareklangiewicz
  id("com.gradle.develocity") version "4.2.2" // https://docs.gradle.com/develocity/gradle-plugin/
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
    publishing.onlyIf { buildScanPublishingAllowed && it.buildResult.failures.isNotEmpty() }
  }
}

// endregion [[My Settings Stuff]]

val enableJs = true
val enableNative = true
// FIXME_someday: how to support all native platforms? Wait/track JetBrains work on "common modules" / "Universal libraries":
//   https://youtrack.jetbrains.com/issue/KT-52666/Kotlin-Multiplatform-libraries-without-platform-specific-code-a.k.a.-Pure-Kotlin-libraries-Universal-libraries

gradle.extLib = lib(
  info = myLibInfo(
    name = "USpek",
    description = "Micro tool for testing with syntax similar to Spek, but shorter.",
    githubUrl = "https://github.com/mareklangiewicz/USpek",
    version = Ver(0, 0, 46),
    // https://central.sonatype.com/artifact/pl.mareklangiewicz/uspek/versions
    // https://github.com/mareklangiewicz/USpek/releases
  ),
  flags = LibFlags(
    withJs = enableJs,
    withLinuxX64 = enableNative,
    withTestJUnit4 = false,
    withTestJUnit5 = false,
    withTestUSpekX = false, // Let's NOT try to test uspek with other packaged uspek to avoid confusion.
    withCentralPublish = true,
  ),
  withCompose = false, // was: compose = null
  // andro is absent by default
)

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
  ":ktandrosample",
)


/*

:ktandrosample was disabled here for a long time, because releasing USpek with it enabled failed:
looked like a bug in task dependencies around resources, never reproduced locally, suspected to be
a race between tasks. Re-enabled on the templatefun migration (deps.settings 0.4.62).

What was actually checked before re-enabling it, on this machine:
- :ktandrosample:build green;
- :ktandrosample:compileAndroidDeviceTest green -- and proved real by planting a type error in
  SomeComposeUSpek.kt and watching that exact task fail, since `build` alone never reaches the
  device-test compilation;
- :ktandrosample:publishToMavenLocal green, signing included, which is the publication assembly the
  old release died in. Residue removed from ~/.m2 afterwards.

That last check is now HISTORY, not something to re-run: the sample modules no longer apply
plugs.VannikPublish, so :ktandrosample has no publication and no publishToMavenLocal work at all.
See the sibling note below about why they had one in the first place.

So the failure does not reproduce here -- but it never did, which is the whole problem. It failed in
CI, on the release workflow, not locally. If drelease goes red again on this module, THIS is the
history, and disabling the include below is the known way back.

original report from github:
https://github.com/mareklangiewicz/USpek/actions/runs/10130733354/job/28012490261
https://scans.gradle.com/s/qtvw3gn3xdqt2

*/

/*

Why no kt*sample module applies plugs.VannikPublish:

They are sample APPS, not libraries, but each one used to apply the publish plugin and so got a
full signed maven publication. Two things followed, measured on 2026-09-18:

- `./gradlew publishToMavenLocal` failed in :ktjsreactsample, because its jsMain carries
  enforcedPlatform(kotlin-wrappers-bom) and Gradle refuses to write an enforced platform into
  published module metadata. NOT a migration regression: the same task fails identically on
  8a75b28, pre-templatefun. It stayed hidden because drelease only runs publishAndReleaseToMavenCentral.

- Worse, and this one WAS a migration regression: in the nested model each sample built a fresh
  LibSettings, so withCentralPublish fell to its default false. In the sibling model each sample
  does gradle.extLib.copy(flags = flags.copy(..)) and inherits the root's `true`. :ktlinuxsample
  went from 0 to 8 mavenCentral tasks. The next v* tag would have pushed six sample artifacts to
  Maven Central under pl.mareklangiewicz, permanently.

Dropping the plugin removes the publications, so both go away. If a sample ever SHOULD be published
(a @sample sources artifact is the plausible case), fix the enforcedPlatform first -- as a published
dependency it forces versions on every consumer -- and pick the artifactId deliberately, since all
six currently default to their directory name with a POM <name> of "ktsample".

Design note for the durable fix, in DepsKt: docs/design/publish-intent-per-module.md

*/
