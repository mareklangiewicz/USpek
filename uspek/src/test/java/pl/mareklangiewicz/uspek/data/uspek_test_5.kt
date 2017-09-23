package pl.mareklangiewicz.uspek.data

import pl.mareklangiewicz.uspek.USpek
import pl.mareklangiewicz.uspek.USpek.o
import org.junit.Assert

fun uspek_test_5() {
    USpek.uspek("some test") {
        "some nested test" o {
            Assert.assertTrue(false)
        }
    }
}