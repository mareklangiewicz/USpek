package mareklangiewicz.pl.uspek

import org.junit.Assert
import org.junit.Test

class TreeCollectionLoggerTest {

    @Test
    fun `should create single success node test tree`() {
        val logger = TreeCollectionLogger()
        val location = CodeLocation("test.kt", 1)
        logger(Report.Start("first test", location))
        logger(Report.Success(location))
        Assert.assertEquals(logger.testTree!!, TestTree(name = "first test", state = TestState.SUCCESS, location = location))
    }
}