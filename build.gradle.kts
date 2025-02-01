
// region [[Full Root Build Imports and Plugs]]

import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.deps.*

plugins {
  plug(plugs.KotlinMulti) apply false
  plug(plugs.KotlinJvm) apply false
  plug(plugs.KotlinMultiCompose) apply false
  plug(plugs.ComposeJb) apply false // ComposeJb(Edge) is very slow to sync, clean, build (jb dev repo issue)
  plug(plugs.AndroLib) apply false
  plug(plugs.AndroApp) apply false
  plug(plugs.NexusPublish)
}

// endregion [[Full Root Build Imports and Plugs]]

val enableJs = true
val enableNative = true
// FIXME_someday: how to support all native platforms? Wait/track JetBrains work on "common modules" / "Universal libraries":
//   https://youtrack.jetbrains.com/issue/KT-52666/Kotlin-Multiplatform-libraries-without-platform-specific-code-a.k.a.-Pure-Kotlin-libraries-Universal-libraries

defaultBuildTemplateForRootProject(
  myLibDetails(
    name = "USpek",
    description = "Micro tool for testing with syntax similar to Spek, but shorter.",
    githubUrl = "https://github.com/mareklangiewicz/USpek",
    version = Ver(0, 0, 40),
    // https://s01.oss.sonatype.org/content/repositories/releases/pl/mareklangiewicz/uspek/
    // https://github.com/mareklangiewicz/USpek/releases
    settings = LibSettings(
      withJs = enableJs,
      withNativeLinux64 = enableNative,
      compose = null,
      withTestJUnit4 = false,
      withTestJUnit5 = false,
      withTestUSpekX = false, // Let's NOT try to test uspek with other packaged uspek to avoid confusion.
      withSonatypeOssPublishing = true,
    ),
  ),
)

// region [[Root Build Template]]

/** Publishing to Sonatype OSSRH has to be explicitly allowed here, by setting withSonatypeOssPublishing to true. */
fun Project.defaultBuildTemplateForRootProject(details: LibDetails? = null) {
  ext.addDefaultStuffFromSystemEnvs()
  details?.let {
    rootExtLibDetails = it
    defaultGroupAndVerAndDescription(it)
    if (it.settings.withSonatypeOssPublishing) defaultSonatypeOssNexusPublishing()
  }

  // kinda workaround for kinda issue with kotlin native
  // https://youtrack.jetbrains.com/issue/KT-48410/Sync-failed.-Could-not-determine-the-dependencies-of-task-commonizeNativeDistribution.#focus=Comments-27-5144160.0-0
  repositories { mavenCentral() }
}

/**
 * System.getenv() should contain six env variables with given prefix, like:
 * * MYKOTLIBS_signing_keyId
 * * MYKOTLIBS_signing_password
 * * MYKOTLIBS_signing_keyFile (or MYKOTLIBS_signing_key with whole signing key)
 * * MYKOTLIBS_ossrhUsername
 * * MYKOTLIBS_ossrhPassword
 * * MYKOTLIBS_sonatypeStagingProfileId
 * * First three of these used in fun pl.mareklangiewicz.defaults.defaultSigning
 * * See KGround/template-full/template-full-lib/build.gradle.kts
 */
fun ExtraPropertiesExtension.addDefaultStuffFromSystemEnvs(envKeyMatchPrefix: String = "MYKOTLIBS_") =
  addAllFromSystemEnvs(envKeyMatchPrefix)

fun Project.defaultSonatypeOssNexusPublishing(
  sonatypeStagingProfileId: String = rootExtString["sonatypeStagingProfileId"],
  ossrhUsername: String = rootExtString["ossrhUsername"],
  ossrhPassword: String = rootExtString["ossrhPassword"],
) {
  nexusPublishing {
    this.repositories {
      sonatype {  // only for users registered in Sonatype after 24 Feb 2021
        stagingProfileId put sonatypeStagingProfileId
        username put ossrhUsername
        password put ossrhPassword
        nexusUrl put repos.sonatypeOssNexus
        snapshotRepositoryUrl put repos.sonatypeOssSnapshots
      }
    }
  }
}

// endregion [[Root Build Template]]
