package pl.mareklangiewicz.ktsample

import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.uspek.o
import pl.mareklangiewicz.uspek.ox
import pl.mareklangiewicz.uspek.uspekTestFactory

class USpekJUnitFactoryNestedTest {

    @TestFactory
    fun uspekExampleTestFactory() = uspekTestFactory {
        "On big test structure" o {
            "Generated 3 passing tests" o {
                for (i in 1..3) "generated passing test $i" o { println("inside generated passing test $i") }
            }
            "Some less dynamic tree" o {
                "Nested a" o {
                    "Nested b" o {
                        "Nested c" o {
                            "Nested d" o {
                                "Nested e" o {
                                    @Suppress("Deprecation")
                                    "Nested f with intentional error" ox {
                                        error("Intentional error")
                                    }
                                }
                            }
                            "Another Nested d" o {
                                "Nested e again" o {
                                    @Suppress("Deprecation")
                                    "failing MicroCalc test" ox {
                                        val calc = MicroCalc(10)
                                        calc.ensureResultIs(20)
                                    }
                                }
                            }
                        }
                    }
                    @Suppress("Deprecation")
                    "Generated 3 failing tests" ox {
                        for (i in 1..3) "generated failing test $i" o { error("generated failing test $i") }
                    }
                    "some passing test" o {}
//                    error("Another fail")
                }
            }
        }
    }
}

