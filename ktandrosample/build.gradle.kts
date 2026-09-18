
// region [[Andro Lib Build Imports and Plugs]]

import com.android.build.api.dsl.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.templatefun.*

plugins {
  id("pl.mareklangiewicz.templatefun")
  plugAll(
    plugs.KotlinMulti,
    plugs.KotlinMultiCompose,
    plugs.ComposeJbNoVer,
    plugs.AndroKmpNoVer,
  )
}

// endregion [[Andro Lib Build Imports and Plugs]]

// The only module in this repo with compose and andro, so it opens both scopes itself instead of
// the root lib carrying them -- every other module requires lib.compose == null.
val androFlags = gradle.extLib.flags.copy(
  withJs = false,
  withLinuxX64 = false,
  withTestJUnit4 = true,
  withTestJUnit5 = false,
  // Device tests take JUnit4 through their OWN flag -- the plain withTestJUnit4 does not reach that
  // configuration. Without this, @RunWith(USpekJUnit4Runner) in androidDeviceTest does not resolve.
  withTestJUnit4OnAndroidDevice = true,
  // Deliberately the PUBLISHED uspekx/uspekx-junit4, not project(":uspekx-junit4"): this sample
  // exists to exercise USpek the way a consumer gets it. Was the same before the migration.
  withTestUSpekX = true,
)

defaultBuildTemplateForAndroLib(
  lib = gradle.extLib.copy(
    info = gradle.extLib.info.copy(
      name = "ktsample",
      namespace = "pl.mareklangiewicz.ktsample",
      appMainPackage = "pl.mareklangiewicz.ktsample",
    ),
    flags = androFlags,
    // Derived from THIS module's flags, so compose's ui-test-junit4 follows withTestJUnit4 -- which
    // is what createComposeRule() in the device test needs.
    compose = defaultLibCompose(androFlags),
    // publishVariant is gone from LibAndro as of DepsKt 0.4.63 -- it was a publishing decision
    // living on a per-repo settings object. It is LibPublish(androVariant = ..) now, and this
    // module passes no LibPublish at all, because a SAMPLE app is not published. Nothing is lost:
    // the variant only ever mattered for registering a publishable android component.
    andro = LibAndro(),
  ),
)

// The old script's explicit dependencies { defaultAndroTestDeps(configuration =
// "androidTestImplementation") } block is gone: since AGP 9 an android library is a KMP module, the
// configuration is androidDeviceTestImplementation, and defaultBuildTemplateForAndroLib wires both
// it and androidHostTestImplementation itself.
