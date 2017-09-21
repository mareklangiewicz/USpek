package mareklangiewicz.pl.uspek.data

import mareklangiewicz.pl.uspek.USpek
import mareklangiewicz.pl.uspek.USpek.o
import org.junit.Assert

fun uspek_test_4() {
    USpek.uspek("some test") {
        "some assertion" o {
            Assert.assertTrue(false)
        }
    }
}