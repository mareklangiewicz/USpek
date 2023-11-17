package pl.mareklangiewicz.ktsample

import pl.mareklangiewicz.uspek.*
import kotlin.test.*

class MicroCalcCmnTest {
    @Test fun microCalcCmnTest() = uspek { testSomeMicroCalc() }
    @Test fun loggingCmnTest() = uspek { testSomeLogging() }
}
