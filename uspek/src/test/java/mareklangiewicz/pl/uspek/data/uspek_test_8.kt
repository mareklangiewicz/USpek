package mareklangiewicz.pl.uspek.data

import mareklangiewicz.pl.uspek.USpek
import mareklangiewicz.pl.uspek.USpek.o
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