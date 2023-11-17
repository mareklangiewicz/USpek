package pl.mareklangiewicz.ktsample

import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.uspek.uspekTestFactory

class USpekJUnitFactoryNestedTest {
    @TestFactory fun uspekExampleTestFactory() = uspekTestFactory { testSomeDeepNestedStructure() }
}

