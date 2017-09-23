package pl.mareklangiewicz.uspek.data

import pl.mareklangiewicz.uspek.USpek
import pl.mareklangiewicz.uspek.USpek.o
import org.junit.Assert

fun uspek_test_4() {
    USpek.uspek("some test") {
        "some assertion" o {
            Assert.assertTrue(false)
        }
    }
}