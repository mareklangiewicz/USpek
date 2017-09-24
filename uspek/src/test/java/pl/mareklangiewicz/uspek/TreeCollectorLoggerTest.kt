package pl.mareklangiewicz.uspek

import org.junit.Assert.assertEquals
import org.junit.Test

class TreeCollectorLoggerTest {
    private val logger = TreeCollectorLogger()

    @Test
    fun `should create single success node test tree`() {
        val location = CodeLocation("test.kt", 1)
        logger(Report.Start("first test", location))
        logger(Report.Success(location))
        assertEquals(TestTree(name = "first test", state = TestState.SUCCESS, location = location), logger.testTree!!)
    }

    @Test
    fun `should create single failure node test tree`() {
        val location = CodeLocation("test.kt", 1)
        val assertionLocation = CodeLocation("test.kt", 2)
        val failureCause = RuntimeException()
        logger(Report.Start("first test", location))
        logger(Report.Failure(location, assertionLocation = assertionLocation, cause = failureCause))
        assertEquals(
                TestTree(name = "first test",
                        state = TestState.FAILURE,
                        location = location,
                        assertionLocation = assertionLocation,
                        failureCause = failureCause),
                logger.testTree!!)
    }

    @Test
    fun `should subtree with single children`() {
        val location = CodeLocation("test.kt", 1)
        logger(Report.Start("suite", location))
        logger(Report.Start("first test", location))
        logger(Report.Success(location))
        logger(Report.Success(location))
        assertEquals(
                TestTree(name = "suite",
                        state = TestState.SUCCESS,
                        location = location,
                        subtests = mutableListOf(TestTree(name = "first test",
                                location = location,
                                state = TestState.SUCCESS))),
                logger.testTree!!)
    }

    @Test
    fun `should create subtree with two childrens`() {
        val location = CodeLocation("test.kt", 1)
        val firstTestLocation = location.copy(lineNumber = 2)
        val secondTestLocation = location.copy(lineNumber = 3)
        logger(Report.Start("suite", location))
        logger(Report.Start("first test", firstTestLocation))
        logger(Report.Success(firstTestLocation))
        logger(Report.Start("second test", secondTestLocation))
        logger(Report.Success(secondTestLocation))
        logger(Report.Success(location))
        assertEquals(
                TestTree(name = "suite",
                        state = TestState.SUCCESS,
                        location = location,
                        subtests = mutableListOf(
                                TestTree(name = "first test",
                                        location = firstTestLocation,
                                        state = TestState.SUCCESS),
                                TestTree(name = "second test",
                                        location = secondTestLocation,
                                        state = TestState.SUCCESS))),
                logger.testTree!!)
    }

    @Test
    fun `should handle multiple nesting`() {
        val location = CodeLocation("test.kt", 1)
        val firstTestLocation = location.copy(lineNumber = 2)
        val secondTestLocation = location.copy(lineNumber = 3)
        logger(Report.Start("suite", location))
        logger(Report.Start("first test", firstTestLocation))
        logger(Report.Start("second test", secondTestLocation))
        logger(Report.Success(secondTestLocation))
        logger(Report.Start("first test", firstTestLocation))
        logger(Report.Success(firstTestLocation))
        logger(Report.Success(location))
        assertEquals(
                TestTree(name = "suite",
                        state = TestState.SUCCESS,
                        location = location,
                        subtests = mutableListOf(
                                TestTree(name = "first test",
                                        location = firstTestLocation,
                                        state = TestState.SUCCESS,
                                        subtests = mutableListOf(TestTree(name = "second test",
                                                location = secondTestLocation,
                                                state = TestState.SUCCESS))))),
                logger.testTree!!)
    }
}