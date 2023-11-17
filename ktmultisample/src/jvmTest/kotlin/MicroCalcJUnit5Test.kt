package pl.mareklangiewicz.ktsample

import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.kground.*
import pl.mareklangiewicz.uspek.*


class MicroCalcJUnit5Test {
    init { "INIT ${this::class.simpleName}".teePP }
    @TestFactory fun microCalcJUnit5Test() = uspekTestFactory { testSomeMicroCalc() }
    @TestFactory fun loggingTest() = uspekTestFactory { testSomeLogging() }
}
