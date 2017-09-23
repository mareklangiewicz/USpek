package pl.mareklangiewicz.uspek.data

import pl.mareklangiewicz.uspek.USpek
import pl.mareklangiewicz.uspek.USpek.o
import org.junit.Assert

fun uspek_test_8() {
    USpek.uspek("some test") {
        "first test" o {
            Assert.assertTrue(false)
        }

        "second test" o {
            Assert.assertTrue(false)
        }
    }
}