
// region [[Full Root Build Imports and Plugs]]

import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.templatefun.*

plugins {
  plug(plugs.TemplateFun) apply false
  plug(plugs.KotlinMulti) apply false
  plug(plugs.KotlinJvm) apply false
  plug(plugs.KotlinMultiCompose) apply false

  plug(plugs.ComposeJbStable) apply false // ComposeJbEdge can be very slow to sync, clean, build (jb dev repo issue)
  // id("org.jetbrains.compose") version "1.10.0-beta02" apply false
  // TODO_later: Check again after compose update, because now default version fails with:
  // Cannot determine the version of Skiko for Compose '1.10.0-rc01'

  plug(plugs.AndroKmp) apply false
  plug(plugs.AndroApp) apply false
  plug(plugs.VannikPublish) apply false
}

// endregion [[Full Root Build Imports and Plugs]]

defaultGroupAndVerAndDescription(gradle.extLib)
