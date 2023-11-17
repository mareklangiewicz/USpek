package pl.mareklangiewicz.ktsample

import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.kground.*
import pl.mareklangiewicz.uspek.uspekTestFactory

class USpekJUnitFactoryNestedTest {
    init { "INIT ${this::class.simpleName}".teePP }
    @TestFactory fun uspekExampleTestFactory() = uspekTestFactory { testSomeDeepNestedStructure() }
}

