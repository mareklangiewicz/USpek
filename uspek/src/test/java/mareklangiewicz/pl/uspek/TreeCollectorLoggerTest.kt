package mareklangiewicz.pl.uspek

import mareklangiewicz.pl.uspek.loggers.TestState
import mareklangiewicz.pl.uspek.loggers.TestTree
import mareklangiewicz.pl.uspek.loggers.TreeCollectorLogger
import org.junit.Assert
import org.junit.Test

class TreeCollectorLoggerTest {

    @Test
    fun `should create single success node test tree`() {
        val logger = TreeCollectorLogger()
        val location = CodeLocation("test.kt", 1)
        logger(Report.Start("first test", location))
        logger(Report.Success(location))
        Assert.assertEquals(logger.testTree!!, TestTree(name = "first test", state = TestState.SUCCESS, location = location))
    }
}