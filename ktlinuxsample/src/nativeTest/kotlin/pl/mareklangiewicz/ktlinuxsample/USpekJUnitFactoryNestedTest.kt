package pl.mareklangiewicz.ktlinuxsample

import pl.mareklangiewicz.uspek.o
import pl.mareklangiewicz.uspek.ox
import pl.mareklangiewicz.uspek.uspek
import kotlin.test.Test

class USpekJUnitFactoryNestedTest {

    @Test
    fun uspekExampleTestFactory() = uspek {
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
                                    "Nested f with intentional error" ox {
                                        error("Intentional error")
                                    }
                                }
                            }
                            "Another Nested d" o {
                                "Nested e again" o {
                                    "failing MicroCalc test" ox {
                                        val calc = MicroCalc(10)
                                        calc.ensureResultIs(20)
                                    }
                                }
                            }
                        }
                    }
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

