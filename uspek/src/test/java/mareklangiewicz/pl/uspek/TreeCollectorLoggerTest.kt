package mareklangiewicz.pl.uspek

import mareklangiewicz.pl.uspek.loggers.TestState
import mareklangiewicz.pl.uspek.loggers.TestTree
import mareklangiewicz.pl.uspek.loggers.TreeCollectorLogger
import org.junit.Assert.assertEquals
import org.junit.Test

class TreeCollectorLoggerTest {
    private val logger = TreeCollectorLogger()

    @Test
    fun `should create single success node test tree`() {
        val location = CodeLocation("test.kt", 1)
        logger(Report.Start("first test", location))
        logger(Report.Success(location))
        assertEquals(logger.testTree!!, TestTree(name = "first test", state = TestState.SUCCESS, location = location))
    }

    @Test
    fun `should create single failure node test tree`() {
        val location = CodeLocation("test.kt", 1)
        val assertionLocation = CodeLocation("test.kt", 2)
        val failureCause = RuntimeException()
        logger(Report.Start("first test", location))
        logger(Report.Failure(location, assertionLocation = assertionLocation, cause = failureCause))
        assertEquals(logger.testTree!!,
                TestTree(name = "first test",
                        state = TestState.FAILURE,
                        location = location,
                        assertionLocation = assertionLocation,
                        failureCause = failureCause))
    }
}