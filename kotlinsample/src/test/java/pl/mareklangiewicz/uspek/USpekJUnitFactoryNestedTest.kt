package pl.mareklangiewicz.uspek

import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.net.URI

class USpekJUnitFactoryNestedTest {

    @TestFactory
    fun uspekExampleTestFactory() = uspekTestFactory {
        "On big test structure" o {
            "Generated 10 passing tests" o {
                for (i in 1..10) "generated passing test $i" o { println("inside generated passing test $i") }
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
                    "Generated 10 failing tests" ox {
                        for (i in 1..10) "generated failing test $i" o { error("generated failing test $i") }
                    }
                    "some passing test" o {}
//                    error("Another fail")
                }
            }
        }
    }
}


// TODO: move implementation below to uspek-junit

fun uspekTestFactory(code: () -> Unit): DynamicNode {
    uspek(code)
    return uspekContext.root.dnode
}

private val USpekTree.dnode: DynamicNode get() {
    val nodes = branches.values.map { it.dnode }
    return when {
        nodes.isEmpty() -> dtest
        failed -> dynamicContainer(name, nodes + dtest)
        else -> dynamicContainer(name, nodes)
    }
}

private val USpekTree.dtest get() = dynamicTest(name) { end?.cause?.let { throw it } }

// TODO: use JUnit5 URIs: https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests-uri-test-source
//   to be able to click (or F4) on the test in the Intellij test tree and to be navigated to exact test line
// TODO: check why this doesn't do the trick (or similar for dynamicContainer):
//   dynamicTest(name, location?.tsource) { end?.cause?.let { throw it } }
private val CodeLocation.tsource get() = URI("classpath:/$fileName?line=$lineNumber")

