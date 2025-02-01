package pl.mareklangiewicz.ktsample

import pl.mareklangiewicz.kground.*
import pl.mareklangiewicz.uspek.*
import kotlin.test.*
import pl.mareklangiewicz.kground.tee.teePP

class USpekExampleCmnNestedTest {
  init {
    "INIT ${this::class.simpleName}".teePP
  }

  @Test fun uspekExampleCmnNestedTest() = uspek { testSomeDeepNestedStructure() }
}

