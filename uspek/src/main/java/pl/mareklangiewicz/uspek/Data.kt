package pl.mareklangiewicz.uspek

data class CodeLocation(val fileName: String, val lineNumber: Int) {
    override fun toString() = "$fileName:$lineNumber"
}

enum class TestState { STARTED, SUCCESS, FAILURE }

data class TestInfo(
        var name: String? = null,
        var trace: TestTrace? = null,
        var state: TestState? = null,
        var failureLocation: CodeLocation? = null,
        var failureCause: Throwable? = null,
        var data: Any? = null
)

val TestInfo.location get () = trace?.get(0)?.location

fun TestInfo.applyAllFrom(info: TestInfo) {
    name = info.name
    trace = info.trace
    state = info.state
    failureLocation = info.failureLocation
    failureCause = info.failureCause
    data = info.data
}

fun TestInfo.applyExistentFrom(info: TestInfo) {
    name = info.name ?: name
    trace = info.trace ?: trace
    state = info.state ?: state
    failureLocation = info.failureLocation ?: failureLocation
    failureCause = info.failureCause ?: failureCause
    data = info.data ?: data
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
