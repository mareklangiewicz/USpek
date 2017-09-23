package pl.mareklangiewicz.uspek.data

import pl.mareklangiewicz.uspek.USpek
import pl.mareklangiewicz.uspek.USpek.o
import org.junit.Assert

fun uspek_test_10() {
    USpek.uspek("some test") {
        "first test" o {
            Assert.assertTrue(true)

            "second test" o {
                Assert.assertTrue(true)
            }
        }
    }
}