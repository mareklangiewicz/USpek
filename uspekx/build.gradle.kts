
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

defaultBuildTemplateForBasicMppLib(publish = LibPublish(toCentral = true)) {
  api(project(":uspek"))
  api(KotlinX.coroutines_core)
  api(KotlinX.coroutines_test)
}

kotlin {
  // TODO_later: add concurrentMain and concurrentTest to default templates under flags.
  //    but first check it all here if it actually work mpp
  applyDefaultHierarchyTemplate()
  // see: https://kotlinlang.org/docs/multiplatform-hierarchy.html#creating-additional-source-sets
  sourceSets {
    val concurrentMain by creating {
      dependsOn(commonMain.get())
    }
    val jvmMain by getting {
      dependsOn(concurrentMain)
    }
    val nativeMain by getting {
      dependsOn(concurrentMain)
    }
  }
}
