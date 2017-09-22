package mareklangiewicz.pl.uspek

class TreeCollectionLogger : (USpek.Report) -> Unit {
    var testTree: TestTree? = null
        private set

    private var currentTest: TestTree? = null

    override fun invoke(report: USpek.Report) {
        when (report) {

            is USpek.Report.Start -> {
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

            is USpek.Report.Success -> {
                val test = currentTest!!
                check(testTree !== null)
                check(report.testLocation == test.location)
                test.state = TestState.SUCCESS
                currentTest = testTree // now, we will start again from the top
            }

            is USpek.Report.Failure -> {
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
        var location: USpek.CodeLocation? = null,
        var state: TestState = TestState.STARTED,
        var assertionLocation: USpek.CodeLocation? = null,
        var failureCause: Throwable? = null,
        val subtests: MutableList<TestTree> = mutableListOf()
)

