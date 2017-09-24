package pl.mareklangiewicz.uspek

import pl.mareklangiewicz.uspek.TestState.*

typealias ULog = (TestInfo) -> Unit

internal fun logToAll(vararg log: ULog) = fun(info: TestInfo) = log.forEach { it(info) }

internal fun logToList(list: MutableList<TestInfo>) = fun(info: TestInfo) { list.add(info) }

internal fun logToConsole(info: TestInfo) = info.run {
    when (state) {
        STARTED -> println(name)
        SUCCESS -> println("SUCCESS.($location)")
        FAILURE -> {
            println("FAILURE.($location)")
            println("BECAUSE.($failureLocation)")
            println(failureCause)
        }
        null -> println(info.toString()) // unknown state; just print everything we know
    }
}


internal class logToTree(private val tree: TestTree) : ULog {

    private var currentSubTree = tree

    override fun invoke(info: TestInfo) {

        val current = currentSubTree

        if (current === tree && tree.info.state === null) {
            check(info.state == STARTED)
            tree.reset(info)
        }
        else when (info.state) {

            STARTED -> {
                val subTree = current.subtrees.find { it.info.location == info.location }
                if (subTree !== null) {
                    currentSubTree = subTree
                } else {
                    val newSubTree = TestTree(info.copy())
                    current.subtrees.add(newSubTree)
                    currentSubTree = newSubTree
                }
            }

            SUCCESS -> {
                check(info.location == current.info.location)
                current.info.state = SUCCESS
                currentSubTree = tree // start again from the top
            }

            FAILURE -> {
                check(info.location == current.info.location)
                current.info.state = FAILURE
                current.info.failureLocation = info.failureLocation
                current.info.failureCause = info.failureCause
                currentSubTree = tree // start again from the top
            }

            null -> throw IllegalStateException("Unknown test state")
        }
    }
}