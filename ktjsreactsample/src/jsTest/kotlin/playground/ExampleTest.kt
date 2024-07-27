package playground

import kotlinx.coroutines.*
import pl.mareklangiewicz.ktsample.*
import pl.mareklangiewicz.uspek.*
import kotlin.test.*

class ExampleTest {
  // Note: need to use GlobalUSpekContext, because testSomeMicroCalc uses it too (using blocking "o" instead of "so").
  @Test fun exampleMicroCalcTest() = runTestUSpek(GlobalUSpekContext) {
    delay(100) // Pretending we have some suspensions in tests. Should be fast thanks to TestDispatcher anyway.
    testSomeMicroCalc()
  }

  // Note: last test is disabled, but when you enable it, it then correctly fails,
  // but reporting in IntelliJ is incorrect (green mark) (search in logs: FAILURE to see it failed)
  @Test fun exampleBasicTest() = runTestUSpek {
    "2 + 2 = 4" so { 2 + 2 chkEq 4 }
    "2 + 3 = 5" so { 2 + 3 chkEq 5 }
    "2 + 4 = 666" sox { 2 + 3 chkEq 666 }
  }
}
