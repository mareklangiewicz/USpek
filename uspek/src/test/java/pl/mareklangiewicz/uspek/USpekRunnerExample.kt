package pl.mareklangiewicz.uspek

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import pl.mareklangiewicz.uspek.USpek.o

@RunWith(USpekJUnitRunner::class)
class USpekRunnerExample {

    @Test
    fun someTest() {
        USpek.uspek("some nested test") {
            "1st assertion" o {
                Assert.assertTrue(true)
                "nested assertion" o {
                    Assert.assertTrue(true)
                    "even more nested assertion" o {
                        Assert.assertTrue(true)
                    }
                }
            }
            "2nd assertion" o {
                Assert.assertFalse(false)
            }
            "3rd assertion" o {
                Assert.assertFalse(false)
            }
        }
    }
}