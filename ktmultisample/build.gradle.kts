
// region [[Basic MPP App Build Imports and Plugs]]

import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.templatefun.*

plugins {
  id("pl.mareklangiewicz.templatefun")
  plugAll(plugs.KotlinMulti)
}

// endregion [[Basic MPP App Build Imports and Plugs]]

// jvm + js + linuxX64. JUnit5 only: 4 and 5 cannot both be on, since 5 does useJUnitPlatform().
// withTestUSpekX stays false -- the uspek/uspekx bindings come from :project directly below.
defaultBuildTemplateForBasicMppApp(
  lib = gradle.extLib.run {
    copy(
      info = gradle.extLib.info.copy(
        name = "ktsample",
        // namespace AND appMainPackage both have to be spelled out: data-class copy() does not
        // recompute defaulted fields, so neither follows from name, and appMainPackage does not
        // follow from namespace either. Missing the second one builds fine until the native link
        // step, which then looks for pl/mareklangiewicz/uspek/main and cannot find it.
        namespace = "pl.mareklangiewicz.ktsample",
        appMainPackage = "pl.mareklangiewicz.ktsample",
      ).copy(appMainClass = "MainKt"), // was: details.copy(appMainClass = "MainKt")
      flags = flags.copy(
        withLinuxX64 = true,
        withTestJUnit4 = false,
        withTestJUnit5 = true,
        withTestUSpekX = false,
      ),
    )
  },
) {
  implementation(Langiewicz.kground)
  // https://s01.oss.sonatype.org/content/repositories/releases/pl/mareklangiewicz/kground/
}

kotlin {
  sourceSets {
    val commonTest by getting {
      dependencies {
        implementation(project(":uspek"))
        implementation(project(":uspekx"))
      }
    }
    val jvmTest by getting {
      dependencies {
        implementation(project(":uspekx-junit5"))
      }
    }
  }
}
