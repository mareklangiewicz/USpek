package pl.mareklangiewicz.uspek

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.mareklangiewicz.uspek.TestState.*

class TreeCollectorLoggerTest {

    private val logger = TreeCollectorLogger()

    @Test
    fun `should create single success node test tree`() {
        val location = CodeLocation("test.kt", 1)
        logger(TestInfo("first test", location, state = STARTED))
        logger(TestInfo(location = location, state = SUCCESS))
        assertEquals(TestTree(TestInfo(name = "first test", state = SUCCESS, location = location)), logger.testTree!!)
    }

    @Test
    fun `should create single failure node test tree`() {
        val location = CodeLocation("test.kt", 1)
        val assertionLocation = CodeLocation("test.kt", 2)
        val failureCause = RuntimeException()
        logger(TestInfo("first test", location, state = STARTED))
        logger(TestInfo(location = location, state = FAILURE, failureLocation = assertionLocation, failureCause = failureCause))
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
        logger(TestInfo("suite", location, state = STARTED))
        logger(TestInfo("first test", location, state = STARTED))
        logger(TestInfo(location = location, state = SUCCESS))
        logger(TestInfo(location = location, state = SUCCESS))
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
        logger(TestInfo("suite", location, state = STARTED))
        logger(TestInfo("first test", firstTestLocation, state = STARTED))
        logger(TestInfo(location = firstTestLocation, state = SUCCESS))
        logger(TestInfo("second test", secondTestLocation, state = STARTED))
        logger(TestInfo(location = secondTestLocation, state = SUCCESS))
        logger(TestInfo(location = location, state = SUCCESS))
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
        logger(TestInfo("suite", location, state = STARTED))
        logger(TestInfo("first test", firstTestLocation, state = STARTED))
        logger(TestInfo("second test", secondTestLocation, state = STARTED))
        logger(TestInfo(location = secondTestLocation, state = SUCCESS))
        logger(TestInfo("first test", firstTestLocation, state = STARTED))
        logger(TestInfo(location = firstTestLocation, state = SUCCESS))
        logger(TestInfo(location = location, state = SUCCESS))
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