package mareklangiewicz.pl.uspek

import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod

class USpekJUnitRunner(testClass: Class<Any>) : BlockJUnit4ClassRunner(testClass) {

    enum class TestState { STARTED, SUCCESS, FAILURE }

    data class TestTree(
            var name: String = "",
            var location: USpek.CodeLocation? = null,
            var state: TestState = TestState.STARTED,
            var assertionLocation: USpek.CodeLocation? = null,
            var failureCause: Throwable? = null,
            val subtests: MutableList<TestTree> = mutableListOf()
    )

    private var testTree: TestTree? = null

    private var currentTest: TestTree? = null

    init {
        USpek.log = { report ->

            when (report) {

                is USpek.Report.Start -> {
                    if (testTree === null) {
                        check(currentTest === null)
                        testTree = TestTree(report.testName, report.testLocation)
                        currentTest = testTree
                    }
                    else {
                        val test = currentTest!!
                        val subtest = test.subtests.find { it.location == report.testLocation }
                        if (subtest !== null) {
                            currentTest = subtest
                        }
                        else {
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


    private val suite = Description.createSuiteDescription(testClass.simpleName)

    override fun runChild(method: FrameworkMethod, notifier: RunNotifier) {
//        USpek.log = { report ->
//            println("Report: $report")
//            when (report) {
//                is USpek.Report.Start -> {
//                    val testDescription = newTestDescription(method, report.testName)
//                    suite.children.first().addChild(testDescription)
//                    notifier.fireTestStarted(testDescription)
//                    notifier.fireTestFinished(testDescription)
//                }
////                is USpek.Report.Success -> notifier.fireTestFinished(newTestDescription(method, report))
////                is USpek.Report.Failure -> notifier.fireTestFailure(Failure(newTestDescription(method, report), report.cause))
//            }
//        }
//        println("USpek is running ${method.name}")
//        suite.addChild(Description.createSuiteDescription(method.name))
        super.runChild(method, notifier)
    }

    override fun run(notifier: RunNotifier) {
        println("USpek is running....")
        super.run(notifier)
        println(testTree)
    }

//    private fun newTestDescription(method: FrameworkMethod, name: String) =
//            Description.createTestDescription(method.name, name)



}