package mareklangiewicz.pl.uspek.data

import mareklangiewicz.pl.uspek.USpek
import mareklangiewicz.pl.uspek.USpek.o
import org.junit.Assert

fun uspek_test_5() {
    USpek.uspek("some test") {
        "some nested test" o {
            Assert.assertTrue(false)
        }
    }
}