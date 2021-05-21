package playground

import kotlinx.coroutines.delay
import pl.mareklangiewicz.uspek.*

class MicroCalc(var result: Int) {
    fun add(x: Int) { result += x }
    fun multiplyBy(x: Int) { result *= x }
}

// lazy so
private suspend infix fun String.lso(code: suspend () -> Unit) {
    delay(lsoDelayMs)
    so(code)
}

var lsoDelayMs = 100L

suspend fun example() = suspek {
    "create SUT" lso {

        val sut = MicroCalc(10)

        "check add" lso {
            sut.add(5)
            sut.result eq 15
            sut.add(100)
            sut.result eq 115
        }

        "mutate SUT" lso {
            sut.add(1)

            "incorrectly check add - this should fail" lso {
                sut.add(5)
                sut.result eq 15
            }
        }

        "check add again" lso {
            sut.add(5)
            sut.result eq 15
            sut.add(100)
            sut.result eq 115
        }

        testSomeAdding(sut)

        "mutate SUT and check multiplyBy" lso {
            sut.result = 3

            sut.multiplyBy(3)
            sut.result eq 9
            sut.multiplyBy(4)
            sut.result eq 36

            testSomeAdding(sut)

            1 eq 2
        }

        "assure that SUT is intact by any of sub tests above" lso {
            sut.result eq 10
        }
    }

}

private suspend fun testSomeAdding(calc: MicroCalc) {
    val start = calc.result
    "add 5 to $start" lso {
        calc.add(5)
        val afterAdd5 = start + 5
        "result should be $afterAdd5" lso { calc.result eq afterAdd5 }

        "add 7 more" lso {
            calc.add(7)
            val afterAdd5Add7 = afterAdd5 + 7
            "result should be $afterAdd5Add7" lso { calc.result eq afterAdd5Add7 }
        }
    }

    "subtract 3" lso {
        calc.add(-3)
        val afterSub3 = start - 3
        "result should be $afterSub3" lso { calc.result eq afterSub3 }
    }

}
