package pl.mareklangiewicz.ktsample

import pl.mareklangiewicz.kground.*
import pl.mareklangiewicz.uspek.*
import kotlin.test.*

// Note: no runTest or runTestUSpek here, because these tests are not suspendable, but plain synchronous.
// Note: IntelliJ have play buttons allowing to run it only on JVM and Native, but not JS.
//   See jsTest/UnnecessaryMicroCalcJsTest.kt comments for more info about JS issues.
class MicroCalcCmnTest {
    init { "INIT ${this::class.simpleName}".teePP }
    @Test fun microCalcCmnTest() = uspek { testSomeMicroCalc() }
    @Test fun loggingCmnTest() = uspek { testSomeLogging() }
}
