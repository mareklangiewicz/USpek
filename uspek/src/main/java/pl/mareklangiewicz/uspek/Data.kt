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

fun TestInfo.applyAllFrom(info: TestInfo) {
    name = info.name
    location = info.location
    state = info.state
    failureLocation = info.failureLocation
    failureCause = info.failureCause
    description = info.description
}

fun TestInfo.applyExistentFrom(info: TestInfo) {
    name = info.name ?: name
    location = info.location ?: location
    state = info.state ?: state
    failureLocation = info.failureLocation ?: failureLocation
    failureCause = info.failureCause ?: failureCause
    description = info.description ?: description
}

data class TestTree(
        val info: TestInfo = TestInfo(),
        val subtrees: MutableList<TestTree> = mutableListOf()
)

fun TestTree.reset(i: TestInfo = TestInfo()) {
    info.applyAllFrom(i)
    subtrees.clear()
}

class TestEnd(cause: Throwable? = null) : RuntimeException(cause)
