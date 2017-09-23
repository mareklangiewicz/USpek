package pl.mareklangiewicz.uspek

import pl.mareklangiewicz.uspek.USpek.eq
import pl.mareklangiewicz.uspek.USpek.o
import pl.mareklangiewicz.uspek.USpek.uspek

//@RunWith(USpekJUnitRunner::class)
class MicroCalcTest {

    init {
        uspek("MicroCalc tests") {

            "create SUT" o {

                val sut = MicroCalc(10)

                "check add" o {
                    sut.add(5)
                    sut.result eq 15
                    sut.add(100)
                    sut.result eq 115
                }

                "mutate SUT" o {
                    sut.add(1)

                    "incorrectly check add - this should fail" o {
                        sut.add(5)
                        sut.result eq 15
                    }
                }

                "check add again" o {
                    sut.add(5)
                    sut.result eq 15
                    sut.add(100)
                    sut.result eq 115
                }

                "mutate SUT and check multiplyBy" o {
                    sut.result = 3

                    sut.multiplyBy(3)
                    sut.result eq 9
                    sut.multiplyBy(4)
                    sut.result eq 36
                }

                "assure that SUT is intact by any of sub tests above" o {
                    sut.result eq 10
                }
            }

        }
    }

}