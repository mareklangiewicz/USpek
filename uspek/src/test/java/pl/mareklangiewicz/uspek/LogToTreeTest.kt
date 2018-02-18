package pl.mareklangiewicz.uspek

import org.junit.Before

class LogToTreeTest {

    private val tree: TestTree = TestTree()

    private val log = logToTree(tree)

    @Before fun setup() { tree.reset() }

    // TODO 2: Update these tests or rather write new ones using MiniSpek (NSpek?) and no assertj
//    @Test
//    fun `should create single success node test tree`() {
//        val location = CodeLocation("test.kt", 1)
//        log(TestInfo("first test", location, state = STARTED))
//        log(TestInfo(location = location, state = SUCCESS))
//        assertEquals(TestTree(TestInfo(name = "first test", state = SUCCESS, location = location)), tree)
//    }
//
//    @Test
//    fun `should create single failure node test tree`() {
//        val location = CodeLocation("test.kt", 1)
//        val assertionLocation = CodeLocation("test.kt", 2)
//        val failureCause = RuntimeException()
//        log(TestInfo("first test", location, state = STARTED))
//        log(TestInfo(location = location, state = FAILURE, failureLocation = assertionLocation, failureCause = failureCause))
//        assertEquals(
//                TestTree(TestInfo(name = "first test",
//                        state = FAILURE,
//                        location = location,
//                        failureLocation = assertionLocation,
//                        failureCause = failureCause)),
//                tree)
//    }
//
//    @Test
//    fun `should subtree with single children`() {
//        val location = CodeLocation("test.kt", 1)
//        log(TestInfo("suite", location, state = STARTED))
//        log(TestInfo("first test", location, state = STARTED))
//        log(TestInfo(location = location, state = SUCCESS))
//        log(TestInfo(location = location, state = SUCCESS))
//        assertEquals(
//                TestTree(TestInfo(name = "suite",
//                        state = SUCCESS,
//                        location = location),
//                        subtrees = mutableListOf(TestTree(TestInfo(name = "first test",
//                                location = location,
//                                state = SUCCESS)))),
//                tree)
//    }
//
//    @Test
//    fun `should create subtree with two childrens`() {
//        val location = CodeLocation("test.kt", 1)
//        val firstTestLocation = location.copy(lineNumber = 2)
//        val secondTestLocation = location.copy(lineNumber = 3)
//        log(TestInfo("suite", location, state = STARTED))
//        log(TestInfo("first test", firstTestLocation, state = STARTED))
//        log(TestInfo(location = firstTestLocation, state = SUCCESS))
//        log(TestInfo("second test", secondTestLocation, state = STARTED))
//        log(TestInfo(location = secondTestLocation, state = SUCCESS))
//        log(TestInfo(location = location, state = SUCCESS))
//        assertEquals(
//                TestTree(TestInfo(name = "suite",
//                        state = SUCCESS,
//                        location = location),
//                        subtrees = mutableListOf(
//                                TestTree(TestInfo(name = "first test",
//                                        location = firstTestLocation,
//                                        state = SUCCESS)),
//                                TestTree(TestInfo(name = "second test",
//                                        location = secondTestLocation,
//                                        state = SUCCESS)))),
//                tree)
//    }
//
//    @Test
//    fun `should handle multiple nesting`() {
//        val location = CodeLocation("test.kt", 1)
//        val firstTestLocation = location.copy(lineNumber = 2)
//        val secondTestLocation = location.copy(lineNumber = 3)
//        log(TestInfo("suite", location, state = STARTED))
//        log(TestInfo("first test", firstTestLocation, state = STARTED))
//        log(TestInfo("second test", secondTestLocation, state = STARTED))
//        log(TestInfo(location = secondTestLocation, state = SUCCESS))
//        log(TestInfo("first test", firstTestLocation, state = STARTED))
//        log(TestInfo(location = firstTestLocation, state = SUCCESS))
//        log(TestInfo(location = location, state = SUCCESS))
//        assertEquals(
//                TestTree(TestInfo(name = "suite",
//                        state = SUCCESS,
//                        location = location),
//                        subtrees = mutableListOf(
//                                TestTree(TestInfo(name = "first test",
//                                        location = firstTestLocation,
//                                        state = SUCCESS),
//                                        subtrees = mutableListOf(TestTree(TestInfo(name = "second test",
//                                                location = secondTestLocation,
//                                                state = SUCCESS)))))),
//                tree)
//    }
}
