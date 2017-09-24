package pl.mareklangiewicz.uspek

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.mareklangiewicz.uspek.TestState.*

class TreeCollectorLoggerTest {
    object Report { // temporary wrapper for tests
        fun Start(name: String, location: CodeLocation) = TestInfo(name, location, state = STARTED)
        fun Success(testLocation: CodeLocation) = TestInfo(location = testLocation, state = SUCCESS)
        fun Failure(testLocation: CodeLocation, assertionLocation: CodeLocation, cause: Throwable)
                = TestInfo(location = testLocation, state = FAILURE, failureLocation = assertionLocation, failureCause = cause)
    }
    private val logger = TreeCollectorLogger()

    @Test
    fun `should create single success node test tree`() {
        val location = CodeLocation("test.kt", 1)
        logger(Report.Start("first test", location))
        logger(Report.Success(location))
        assertEquals(TestTree(TestInfo(name = "first test", state = SUCCESS, location = location)), logger.testTree!!)
    }

    @Test
    fun `should create single failure node test tree`() {
        val location = CodeLocation("test.kt", 1)
        val assertionLocation = CodeLocation("test.kt", 2)
        val failureCause = RuntimeException()
        logger(Report.Start("first test", location))
        logger(Report.Failure(location, assertionLocation = assertionLocation, cause = failureCause))
        assertEquals(
                TestTree(TestInfo(name = "first test",
                        state = FAILURE,
                        location = location,
                        failureLocation = assertionLocation,
                        failureCause = failureCause)),
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
                TestTree(TestInfo(name = "suite",
                        state = SUCCESS,
                        location = location),
                        subtrees = mutableListOf(TestTree(TestInfo(name = "first test",
                                location = location,
                                state = SUCCESS)))),
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
                TestTree(TestInfo(name = "suite",
                        state = SUCCESS,
                        location = location),
                        subtrees = mutableListOf(
                                TestTree(TestInfo(name = "first test",
                                        location = firstTestLocation,
                                        state = SUCCESS)),
                                TestTree(TestInfo(name = "second test",
                                        location = secondTestLocation,
                                        state = SUCCESS)))),
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
                TestTree(TestInfo(name = "suite",
                        state = SUCCESS,
                        location = location),
                        subtrees = mutableListOf(
                                TestTree(TestInfo(name = "first test",
                                        location = firstTestLocation,
                                        state = SUCCESS),
                                        subtrees = mutableListOf(TestTree(TestInfo(name = "second test",
                                                location = secondTestLocation,
                                                state = SUCCESS)))))),
                logger.testTree!!)
    }
}