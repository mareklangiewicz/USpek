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



