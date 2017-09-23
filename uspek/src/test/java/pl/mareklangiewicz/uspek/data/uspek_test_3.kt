package pl.mareklangiewicz.uspek.data

import pl.mareklangiewicz.uspek.USpek.o
import pl.mareklangiewicz.uspek.USpek.uspek
import org.junit.Assert

fun uspek_test_3() {
    uspek("some test") {
        "some assertion" o {
            Assert.assertTrue(true)
        }
    }
}