package mareklangiewicz.pl.uspek

import org.junit.Assert
import org.junit.Test

class USpekRunnerTest {

    @Test
    fun `should create instance of test class`() {
        USpekJUnitRunner(TestClass::class.java)
        Assert.assertTrue(TestClass.created)
    }

    private class TestClass {
        init {
            created = true
        }

        companion object {
            var created = false
        }
    }
}
