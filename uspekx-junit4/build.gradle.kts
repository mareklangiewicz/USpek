
// region [[Basic MPP Lib Build Imports and Plugs]]

import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import com.vanniktech.maven.publish.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.templatefun.*

plugins {
  id("pl.mareklangiewicz.templatefun")
  plugAll(plugs.KotlinMulti, plugs.VannikPublish)
}

// endregion [[Basic MPP Lib Build Imports and Plugs]]

// JUnit bindings are jvm-only, so this module drops the js and native targets the rest of the repo
// has. Was: rootExtLibDetails.settings.copy(withJs = false, withLinuxX64 = false).
defaultBuildTemplateForBasicMppLib(
  lib = gradle.extLib.run { copy(flags = flags.copy(withJs = false, withLinuxX64 = false)) },
  publish = LibPublish(toCentral = true),
) {
  api(project(":uspekx"))
}

kotlin {
  sourceSets {
    val jvmMain by getting {
      dependencies {
        implementation(JUnit.junit)
      }
    }
  }
}
