package pl.mareklangiewicz.ktsample

import pl.mareklangiewicz.kground.*
import pl.mareklangiewicz.uspek.*
import kotlin.test.*

class MicroCalcCmnTest {
    init { "INIT ${this::class.simpleName}".teePP }
    @Test fun microCalcCmnTest() = uspek { testSomeMicroCalc() }
    @Test fun loggingCmnTest() = uspek { testSomeLogging() }
}
