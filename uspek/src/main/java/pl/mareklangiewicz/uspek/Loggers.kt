package pl.mareklangiewicz.uspek

typealias ULog = (TestInfo) -> Unit

internal fun logToAll(vararg log: ULog) = fun(info: TestInfo) = log.forEach { it(info) }

internal fun logToList(list: MutableList<TestInfo>) = fun(info: TestInfo) { list.add(info) }

internal fun logToConsole(info: TestInfo) = info.run {
    when {
        failed -> {
            println("FAILURE.($location)")
            println("BECAUSE.($failureLocation)")
            println(failureCause)
        }
        finished -> println("SUCCESS.($location)")
        else -> println(name)
    }
}


internal class logToTree(private val tree: TestTree) : ULog {

    private var currentSubTree = tree

    override fun invoke(info: TestInfo) {

        val current = currentSubTree

        if (current === tree && !tree.info.finished && !info.finished)
            tree.reset(info)
        else info.run {
            when {
                finished -> {
                    check(info.trace == current.info.trace)
                    current.info.applyExistentFrom(info)
                    currentSubTree = tree // start again from the top
                }
                else -> {
                    val subTree = current.subtrees.find { it.info.location == info.location }
                    if (subTree !== null)
                        currentSubTree = subTree
                    else {
                        currentSubTree = TestTree(info.copy())
                        current.subtrees.add(currentSubTree)
                    }
                }
            }
        }
    }
}
