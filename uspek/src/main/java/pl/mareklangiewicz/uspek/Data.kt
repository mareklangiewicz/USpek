package pl.mareklangiewicz.uspek

import org.junit.runner.Description

data class CodeLocation(val fileName: String, val lineNumber: Int) {
    override fun toString() = "$fileName:$lineNumber"
}

enum class TestState { STARTED, SUCCESS, FAILURE }

data class TestInfo(
        var name: String? = null,
        var location: CodeLocation? = null,
        var state: TestState? = null,
        var failureLocation: CodeLocation? = null,
        var failureCause: Throwable? = null,
        var description: Description? = null
)

data class TestTree(
        val info: TestInfo = TestInfo(),
        val subtrees: MutableList<TestTree> = mutableListOf()
)

fun TestTree.reset(i: TestInfo = TestInfo()) {
    info.name = i.name
    info.location = i.location
    info.state = i.state
    info.failureLocation = i.failureLocation
    info.failureCause = i.failureCause
    info.description = i.description
}

class TestEnd(cause: Throwable? = null) : RuntimeException(cause)
