@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.extLib

rootProject.name = "USpek"


// region [[My Settings Stuff]]

// https://docs.gradle.org/current/userguide/upgrading_version_9.html#opt_into_gradle_10_behavior_by_disabling_implicit_lookup_in_parent_projects
enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }

  // Opt-in through the environment, so this region is identical in every project and no flag has
  // to live above it and be kept in sync. Unset means off. To enable for one run:
  //   ENABLE_LOCAL_DEPSKT_IN_DIR=/home/marek/code/kotlin/DepsKt ./gradlew build
  val enableLocalDepsKtInDir = System.getenv("ENABLE_LOCAL_DEPSKT_IN_DIR")?.let { File(it).normalize() }
  // The env var reaches nested builds too, so DepsKt's own copy of this region sees it: skip self.
  // Pass a String: this scope's includeBuild takes only String, and a File silently resolves to the
  // outer Settings.includeBuild, a plain composite that never offers DepsKt's PLUGINS.
  if (enableLocalDepsKtInDir != null && enableLocalDepsKtInDir != rootDir.normalize()) {
    logger.warn("Including local build $enableLocalDepsKtInDir")
    includeBuild(enableLocalDepsKtInDir.path)
  }
}

plugins {
  id("pl.mareklangiewicz.deps.settings") version "0.4.71" // https://plugins.gradle.org/search?term=mareklangiewicz
  id("com.gradle.develocity") version "4.6.0" // https://docs.gradle.com/develocity/gradle-plugin/
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
    // Opt-in through the environment; unset means no scan is ever published, which is what keeps
    // private repos safe without anyone remembering to switch them off. A public repo turns it on
    // in its own CI workflow:  ENABLE_BUILD_SCAN_PUBLISHING_ON_FAILURE=true
    // Read into a local at configuration time: `onlyIf` runs at the END of the build, and reading
    // a settings-script top-level `val` from there would capture the script OBJECT, which the
    // configuration cache rejects. A local is captured by value.
    val enabled = System.getenv("ENABLE_BUILD_SCAN_PUBLISHING_ON_FAILURE") == "true"
    publishing.onlyIf { enabled && it.buildResult.failures.isNotEmpty() }
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
    // withCentralPublish is GONE from LibFlags as of DepsKt 0.4.63 -- and THIS repo is why. It was a
    // per-REPO flag on the object every module clones for platform reasons, so each kt*sample's
    // gradle.extLib.copy(flags = ..) inherited it: six sample apps with 8 mavenCentral tasks each,
    // which the v0.0.44 release would have made permanent. Each of the four LIBS now opts in at its
    // own defaultBuildTemplateForBasicMppLib call with publish = LibPublish(toCentral = true), and
    // the samples say nothing, which is how they stay unpublished.
    // See DepsKt/docs/design/publish-intent-per-module.md.
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
