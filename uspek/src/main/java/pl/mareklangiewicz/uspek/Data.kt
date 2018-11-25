package pl.mareklangiewicz.uspek

data class CodeLocation(val fileName: String, val lineNumber: Int) {
    override fun toString() = "$fileName:$lineNumber"
}

data class TestInfo(
        var finished: Boolean,
        var name: String? = null,
        var trace: TestTrace? = null,
        var failureLocation: CodeLocation? = null,
        var failureCause: Throwable? = null,
        var data: Any? = null
) {
    val failed get() = failureCause !== null
}


val TestInfo.location get () = trace?.get(0)?.location

fun TestInfo.applyAllFrom(info: TestInfo) {
    finished = info.finished
    name = info.name
    trace = info.trace
    failureLocation = info.failureLocation
    failureCause = info.failureCause
    data = info.data
}

fun TestInfo.applyExistentFrom(info: TestInfo) {
    finished = info.finished
    name = info.name ?: name
    trace = info.trace ?: trace
    failureLocation = info.failureLocation ?: failureLocation
    failureCause = info.failureCause ?: failureCause
    data = info.data ?: data
}

data class TestTree(
        val info: TestInfo = TestInfo(false),
        val subtrees: MutableList<TestTree> = mutableListOf()
)

fun TestTree.reset(i: TestInfo = TestInfo(false)) {
    info.applyAllFrom(i)
    subtrees.clear()
}

class TestEnd(cause: Throwable? = null) : RuntimeException(cause)
