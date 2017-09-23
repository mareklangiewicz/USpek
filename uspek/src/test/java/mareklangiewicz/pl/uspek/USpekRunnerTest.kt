package mareklangiewicz.pl.uspek

import mareklangiewicz.pl.uspek.USpek.o
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class USpekRunnerTest {

    @Test
    fun `should create instance of test class`() {
        var created = false
        TestClass.code = { created = true }
        USpekJUnitRunner(TestClass::class.java)
        assertTrue(created)
    }

    @Test
    fun `should create description with a class name for an empty test suite`() {
        val runner = USpekJUnitRunner(TestClass::class.java)
        assertEquals("TestClass", runner.description.className)
    }

    @Test
    fun `should uspek description with a name of test`() {
        TestClass.code = {
            USpek.uspek("some test") { }
        }
        val runner = USpekJUnitRunner(TestClass::class.java)
        val testCase = runner.description.children.first()
        assertEquals("some test(mareklangiewicz.pl.uspek.TestClass)", testCase.displayName)
    }

    @Test
    fun `should create test case under test suite`() {
        TestClass.code = {
            USpek.uspek("some nested test") {
                "some assertion" o { }
            }
        }
        val runner = USpekJUnitRunner(TestClass::class.java)
        val testCase = runner.description.children.first().children.first()
        assertEquals("some assertion(mareklangiewicz.pl.uspek.TestClass.some nested test)", testCase.displayName)
        assertTrue(testCase.isTest)
        assertEquals(1, runner.testCount())
    }

    @Test
    fun `should create two test cases under test suite`() {
        TestClass.code = {
            USpek.uspek("some test") {
                "first test" o { }
                "second test" o { }
            }
        }
        val runner = USpekJUnitRunner(TestClass::class.java)
        val testSuite = runner.description.children.first().children
        val first = testSuite.first()
        val second = testSuite[1]
        assertEquals("first test(mareklangiewicz.pl.uspek.TestClass.some test)", first.displayName)
        assertTrue(first.isTest)
        assertEquals("second test(mareklangiewicz.pl.uspek.TestClass.some test)", second.displayName)
        assertTrue(second.isTest)
        assertEquals(2, runner.testCount())
    }

    @Test
    fun `should handle two levels of nesting`() {
        TestClass.code = {
            USpek.uspek("some test") {
                "first test" o {
                    "second test" o { }
                }
            }
        }
        val runner = USpekJUnitRunner(TestClass::class.java)
        val testSuite = runner.description.children.first().children
        val first = testSuite.first()
        val second = first.children.first()
        assertEquals("first test", first.displayName)
        assertTrue(first.isSuite)
        assertEquals("second test(mareklangiewicz.pl.uspek.TestClass.some test.first test)", second.displayName)
        assertTrue(second.isTest)
        assertEquals(1, runner.testCount())
    }

    @Test
    fun `should handle mixed levels of nesting`() {
        TestClass.code = {
            USpek.uspek("some test") {
                "first test" o {
                    "second test" o { }
                }
                "third test" o { }
            }
        }
        val runner = USpekJUnitRunner(TestClass::class.java)
        val testSuite = runner.description.children.first().children
        val first = testSuite.first()
        val second = first.children.first()
        val third = testSuite[1]
        assertEquals("first test", first.displayName)
        assertTrue(first.isSuite)
        assertEquals("second test(mareklangiewicz.pl.uspek.TestClass.some test.first test)", second.displayName)
        assertTrue(second.isTest)
        assertEquals("third test(mareklangiewicz.pl.uspek.TestClass.some test)", third.displayName)
        assertTrue(third.isTest)
        assertEquals(2, runner.testCount())
    }
}

private class TestClass {
    init {
        code?.invoke()
    }

    companion object {
        var code: (() -> Unit)? = null
    }
}