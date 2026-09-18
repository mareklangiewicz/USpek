
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

// Native only: no jvm, no js. withTestUSpekX stays false -- :uspekx comes from :project below.
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
        withJs = false,
        withLinuxX64 = true,
        withTestJUnit4 = false,
        withTestJUnit5 = false,
        withTestUSpekX = false,
      ),
    )
  },
) {
  implementation(project(":uspekx"))
  implementation(Langiewicz.kground)
  // https://s01.oss.sonatype.org/content/repositories/releases/pl/mareklangiewicz/kground/
}
