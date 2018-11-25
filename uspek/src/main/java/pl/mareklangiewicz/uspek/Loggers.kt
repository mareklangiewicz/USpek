package pl.mareklangiewicz.uspek

import pl.mareklangiewicz.uspek.TestState.FAILURE
import pl.mareklangiewicz.uspek.TestState.STARTED
import pl.mareklangiewicz.uspek.TestState.SUCCESS

typealias ULog = (TestInfo) -> Unit

fun ULog.start(name: String, trace: TestTrace) = this(TestInfo(name, trace, STARTED))

fun ULog.end(trace: TestTrace, exception: TestEnd) = this(TestInfo(
        trace = trace,
        state = if (exception.cause === null) SUCCESS else FAILURE,
        failureLocation = exception.causeLocation,
        failureCause = exception.cause
))

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
                if (subTree !== null)
                    currentSubTree = subTree
                else {
                    currentSubTree = TestTree(info.copy())
                    current.subtrees.add(currentSubTree)
                }
            }

            SUCCESS, FAILURE -> {
                check(info.trace == current.info.trace)
                current.info.applyExistentFrom(info)
                currentSubTree = tree // start again from the top
            }

            null -> throw IllegalStateException("Unknown test state")
        }
    }
}
