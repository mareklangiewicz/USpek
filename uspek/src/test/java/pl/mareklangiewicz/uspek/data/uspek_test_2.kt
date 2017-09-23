package pl.mareklangiewicz.uspek.data

import pl.mareklangiewicz.uspek.USpek
import pl.mareklangiewicz.uspek.USpek.o
import org.junit.Assert

fun uspek_test_2() {
    USpek.uspek("some nested test") {
        "some assertion" o {
            Assert.assertTrue(true)
        }
    }
}