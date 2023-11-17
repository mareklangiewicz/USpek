package pl.mareklangiewicz.ktsample

import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.uspek.*


class MicroCalcJUnit5Test {
    @TestFactory fun microCalcJUnit5Test() = uspekTestFactory { testSomeMicroCalc() }
    @TestFactory fun loggingTest() = uspekTestFactory { testSomeLogging() }
}
