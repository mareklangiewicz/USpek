package pl.mareklangiewicz.uspek

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(USpekRunner::class)
class MicroCalcTest {

//    @Test fun microCalcTest() = suspekBlocking {
//
//        "create SUT" so {
//
//            val sut = MicroCalc(10)
//
//            "check add" so {
//                sut.add(5)
//                sut.result eq 15
//                sut.add(100)
//                sut.result eq 115
//            }
//
//            "mutate SUT" so {
//                sut.add(1)
//
//                "incorrectly check add - this should fail" ox {
//                    sut.add(5)
//                    sut.result eq 15
//                }
//            }
//
//            "check add again" so {
//                sut.add(5)
//                sut.result eq 15
//                sut.add(100)
//                sut.result eq 115
//                delay(1000)
//            }
//
//            testSomeAdding(sut)
//
//            "mutate SUT and check multiplyBy" so {
//                sut.result = 3
//
//                sut.multiplyBy(3)
//                sut.result eq 9
//                sut.multiplyBy(4)
//                sut.result eq 36
//
//                testSomeAdding(sut)
//            }
//
//            "assure that SUT is intact by any of sub tests above" o { // Important: no suspending so we can just use "o"
//                sut.result eq 10
//            }
//        }
//    }

    @Test fun loggingTest() =
        runBlocking {
            uspek {

                val sut = MicroCalc(10)

                "blaaaaa" o {
                    sut.result eq 10

                    "blee" o {
                        sut.result eq 10
                    }
                }
            }
        }


    private suspend fun testSomeAdding(calc: MicroCalc) {
        val start = calc.result
        "add 5 to $start" so {
            calc.add(5)
            val afterAdd5 = start + 5
            "result should be $afterAdd5" so { calc.result eq afterAdd5 }

            "add 7 more" so {
                calc.add(7)
                val afterAdd5Add7 = afterAdd5 + 7
                "result should be $afterAdd5Add7" so { calc.result eq afterAdd5Add7 }
            }
        }

        "subtract 3" so {
            calc.add(-3)
            val afterSub3 = start - 3
            "result should be $afterSub3" so { calc.result eq afterSub3 }
        }

    }
}
