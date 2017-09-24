package pl.mareklangiewicz.uspek

import pl.mareklangiewicz.uspek.TestState.*

class TreeCollectorLogger : ULog {

    var testTree: TestTree? = null
        private set

    private var currentSubTree: TestTree? = null

    override fun invoke(info: TestInfo) {

        when (info.state) {

            STARTED -> {
                if (testTree === null) {
                    check(currentSubTree === null)
                    testTree = TestTree(info.copy())
                    currentSubTree = testTree
                } else {
                    val current = currentSubTree!!
                    val subTree = current.subtrees.find { it.info.location == info.location }
                    if (subTree !== null) {
                        currentSubTree = subTree
                    } else {
                        val newSubTree = TestTree(info.copy())
                        current.subtrees.add(newSubTree)
                        currentSubTree = newSubTree
                    }
                }
            }

            SUCCESS -> {
                val current = currentSubTree!!
                check(testTree !== null)
                check(info.location == current.info.location)
                current.info.state = TestState.SUCCESS
                currentSubTree = testTree // now, we will start again from the top
            }

            FAILURE -> {
                val current = currentSubTree!!
                check(testTree !== null)
                check(info.location == current.info.location)
                current.info.state = FAILURE
                current.info.failureLocation = info.failureLocation
                current.info.failureCause = info.failureCause
                currentSubTree = testTree // now, we will start again from the top
            }

            null -> throw IllegalStateException("Unknown test state")
        }
    }
}


