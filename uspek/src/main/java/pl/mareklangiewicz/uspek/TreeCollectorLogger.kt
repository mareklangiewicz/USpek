package pl.mareklangiewicz.uspek

import org.junit.runner.Description

class TreeCollectorLogger : ULog {
    var testTree: TestTree? = null
        private set

    private var currentTest: TestTree? = null

    override fun invoke(report: Report) {
        when (report) {

            is Report.Start -> {
                if (testTree === null) {
                    check(currentTest === null)
                    testTree = TestTree(report.testName, report.testLocation)
                    currentTest = testTree
                } else {
                    val test = currentTest!!
                    val subtest = test.subtests.find { it.location == report.testLocation }
                    if (subtest !== null) {
                        currentTest = subtest
                    } else {
                        val newtest = TestTree(report.testName, report.testLocation)
                        test.subtests.add(newtest)
                        currentTest = newtest
                    }


                }
            }

            is Report.Success -> {
                val test = currentTest!!
                check(testTree !== null)
                check(report.testLocation == test.location)
                test.state = TestState.SUCCESS
                currentTest = testTree // now, we will start again from the top
            }

            is Report.Failure -> {
                val test = currentTest!!
                check(testTree !== null)
                check(report.testLocation == test.location)
                test.state = TestState.FAILURE
                test.assertionLocation = report.assertionLocation
                test.failureCause = report.cause
                currentTest = testTree // now, we will start again from the top
            }
        }
    }
}


enum class TestState { STARTED, SUCCESS, FAILURE }

data class TestTree(
        var name: String = "",
        var location: CodeLocation? = null,
        var state: TestState = TestState.STARTED,
        var assertionLocation: CodeLocation? = null,
        var failureCause: Throwable? = null,
        val subtests: MutableList<TestTree> = mutableListOf(),
        var description: Description? = null
)

