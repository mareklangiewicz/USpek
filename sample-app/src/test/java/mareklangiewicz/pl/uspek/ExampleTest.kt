package mareklangiewicz.pl.uspek

import mareklangiewicz.pl.uspek.USpek.eq
import mareklangiewicz.pl.uspek.USpek.o
import mareklangiewicz.pl.uspek.USpek.uspek
import org.junit.Test

class ExampleTest {

    @Test
    fun addSome() {

        uspek("Example.addSome tests") {

            "create SUT" o {

                val sut = Example(10)

                "check addSome" o {
                    sut.addSome(5) eq 15
                    sut.addSome(999) eq 1009
                }

                "mutate SUT and incorrectly check addSome" o {
                    sut.some += 1

                    sut.addSome(5) eq 15
                    sut.addSome(999) eq 1009
                }

                "check addSome again" o {
                    sut.addSome(5) eq 15
                    sut.addSome(999) eq 1009
                }

                "mutate SUT and check multiplySome" o {
                    sut.some = 3

                    sut.multiplySome(3) eq 9
                    sut.multiplySome(4) eq 12
                }

                "assure that SUT is intact by subtests above" o {
                    sut.some eq 10
                }
            }

        }
    }

}