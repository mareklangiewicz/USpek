
// region [[Basic JVM App Build Imports and Plugs]]

import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import com.vanniktech.maven.publish.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.templatefun.*

plugins {
  id("pl.mareklangiewicz.templatefun")
  plugAll(
    plugs.KotlinJvm,
    plugs.JvmApp,
    plugs.VannikPublish,
  )
}

// endregion [[Basic JVM App Build Imports and Plugs]]

// JVM-only sample: no js, no native. JUnit4 only -- 4 and 5 cannot both be on, since JUnit5 does
// useJUnitPlatform(). withTestUSpekX stays false because the uspekx binding comes from :project
// directly below, not from the published artifact.
defaultBuildTemplateForBasicJvmApp(
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
        withJs = false,
        withLinuxX64 = false,
        withTestJUnit4 = true,
        withTestJUnit5 = false,
        withTestUSpekX = false,
      ),
    )
  },
) {
  implementation(project(":uspekx-junit4"))
  implementation(Langiewicz.kground)
  // https://s01.oss.sonatype.org/content/repositories/releases/pl/mareklangiewicz/kground/
}
