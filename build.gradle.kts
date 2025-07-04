
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
    version = Ver(0, 0, 42),
    // https://central.sonatype.com/artifact/pl.mareklangiewicz/uspek/versions
    // https://github.com/mareklangiewicz/USpek/releases
    settings = LibSettings(
      withJs = enableJs,
      withNativeLinux64 = enableNative,
      compose = null,
      withTestJUnit4 = false,
      withTestJUnit5 = false,
      withTestUSpekX = false, // Let's NOT try to test uspek with other packaged uspek to avoid confusion.
      withCentralPublish = true,
    ),
  ),
)

// region [[Root Build Template]]

fun Project.defaultBuildTemplateForRootProject(details: LibDetails? = null) {
  details?.let {
    rootExtLibDetails = it
    defaultGroupAndVerAndDescription(it)
  }
}

// endregion [[Root Build Template]]
