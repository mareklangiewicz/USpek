
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

// js only: no jvm, no native. withTestUSpekX stays false because this sample wires uspekx in a
// non-default way below -- into jsMain rather than jsTest.
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
      ),
      flags = flags.copy(
        withJvm = false,
        withLinuxX64 = false,
        withTestJUnit4 = false,
        withTestJUnit5 = false,
        withTestUSpekX = false,
      ),
    )
  },
) {
  implementation(Langiewicz.kground)
  // https://s01.oss.sonatype.org/content/repositories/releases/pl/mareklangiewicz/kground/
}

val withLocalUSpekX = true // normally it should be false

kotlin {
  sourceSets {
    val jsMain by getting {
      dependencies {
        // I use USpekX in jsMain source here (and not just jsTest) on purpose.
        // To experiment with react based reporting in browser.
        if (withLocalUSpekX)
          implementation(project(":uspekx")) //
        else
          implementation(Langiewicz.uspekx.withVer(Ver(0, 0, 36))) // FIXME: remove hardcoded ver
          // https://s01.oss.sonatype.org/content/repositories/releases/pl/mareklangiewicz/uspek/
        implementation(KotlinX.coroutines_core)
        implementation(project.dependencies.enforcedPlatform(Org.JetBrains.Kotlin_Wrappers.bom))
        implementation(Org.JetBrains.Kotlin_Wrappers.kotlin_react.withNoVer())
        implementation(Org.JetBrains.Kotlin_Wrappers.kotlin_react_dom.withNoVer())
        // kotlin-styled-next dropped: the kotlin-wrappers BOM stopped managing it (2026.9.2
        // manages 70 artifacts, none of them kotlin-styled*), so .withNoVer() resolved to an
        // empty version and jsNpmAggregated failed. Nothing under src/ imports styled, so this
        // is a removal, not a workaround. To bring it back it needs its own explicit version.
      }
    }
  }
}
