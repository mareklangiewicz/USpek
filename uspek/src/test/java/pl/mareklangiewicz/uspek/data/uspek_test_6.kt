package pl.mareklangiewicz.uspek.data

import pl.mareklangiewicz.uspek.USpek
import pl.mareklangiewicz.uspek.USpek.o

fun uspek_test_6() {
    USpek.uspek("some test") {
        "first test" o {
        }

        "second test" o {
        }
    }
}