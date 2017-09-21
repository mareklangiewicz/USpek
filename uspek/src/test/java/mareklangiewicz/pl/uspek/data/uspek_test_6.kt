package mareklangiewicz.pl.uspek.data

import mareklangiewicz.pl.uspek.USpek
import mareklangiewicz.pl.uspek.USpek.o

fun uspek_test_6() {
    USpek.uspek("some test") {
        "first test" o {
        }

        "second test" o {
        }
    }
}