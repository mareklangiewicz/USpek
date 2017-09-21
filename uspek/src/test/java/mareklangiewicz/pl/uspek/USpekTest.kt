package mareklangiewicz.pl.uspek

import mareklangiewicz.pl.uspek.USpek.eq
import mareklangiewicz.pl.uspek.USpek.o
import mareklangiewicz.pl.uspek.USpek.uspek
import org.junit.Assert.assertEquals
import org.junit.Test

class USpekTest {

    @Test
    fun uspekTest() {

        uspek("basic uspek tests") {

            "create some mutable list" o {

                val list = mutableListOf(0, 1, 2)

                assertEquals(3, list.size)

                "check list details" o {
                    assertEquals(0, list[0])
                    assertEquals(1, list[1])
                    assertEquals(2, list[2])
                }

                "add some elements to the list" o {
                    list.add(3)
                    list.add(4)

                    assertEquals(3, list[3])
                    assertEquals(4, list[4])
                    assertEquals(5, list.size)
                }

                "remove middle element from the list" o {
                    list.removeAt(1)

                    "try to check not existing element - it should fail" o {
                        assertEquals(2, list[2])
                    }

                    // this will still work even when the sub test above fails
                    "correctly check the list after removing middle element" o {
                        assertEquals(2, list.size)
                        assertEquals(0, list[0])
                        assertEquals(2, list[1])
                    }

                    "use custom assertion to generate some error" o {
                        list.size eq 666 // it should report error with correct line number
                    }
                }
            }
        }
    }

    @Test
    fun uspekFailingTest() {
        uspek("the middle part should fail but all tests should be executed anyway") {
            "test 1 - passing" o { 1 eq 1 }
            "test 2 - failing" o { 1 eq 2 }
            "test 3 - passing" o { 1 eq 1 }
        }
    }
}