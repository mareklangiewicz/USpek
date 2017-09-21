package mareklangiewicz.pl.uspek.data

import mareklangiewicz.pl.uspek.USpek.o
import mareklangiewicz.pl.uspek.USpek.uspek
import org.junit.Assert

fun uspek_test_3() {
    uspek("some test") {
        "some assertion" o {
            Assert.assertTrue(true)
        }
    }
}