package pl.mareklangiewicz.uspek

import pl.mareklangiewicz.uspek.TestState.*

class TreeCollectorLogger : ULog {

    val tree: TestTree = TestTree()

    private var currentSubTree: TestTree? = null

    fun reset() {
        tree.reset(TestInfo())
        currentSubTree = null
    }

    override fun invoke(info: TestInfo) {

        val current = currentSubTree

        if (current === null) {
            check(info.state == STARTED)
            tree.reset(info)
            currentSubTree = tree
            return
        }

        when (info.state) {

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
                current.info.state = TestState.SUCCESS
                currentSubTree = tree // now, we will start again from the top
            }

            FAILURE -> {
                check(info.location == current.info.location)
                current.info.state = FAILURE
                current.info.failureLocation = info.failureLocation
                current.info.failureCause = info.failureCause
                currentSubTree = tree // now, we will start again from the top
            }

            null -> throw IllegalStateException("Unknown test state")
        }
    }

    private fun TestTree.reset(i: TestInfo) {
        info.name = i.name
        info.location = i.location
        info.state = i.state
        info.failureLocation = i.failureLocation
        info.failureCause = i.failureCause
        info.description = i.description
    }
}


