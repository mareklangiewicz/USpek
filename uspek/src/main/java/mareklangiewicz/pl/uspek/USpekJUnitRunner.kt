package mareklangiewicz.pl.uspek

import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod
import java.util.*

class USpekJUnitRunner(private val testClass: Class<Any>) : BlockJUnit4ClassRunner(testClass) {

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


    private val suite = Description.createSuiteDescription(testClass.simpleName, UUID.randomUUID().toString())

    override fun runChild(method: FrameworkMethod, notifier: RunNotifier) {
        super.runChild(method, notifier)
    }

    override fun run(notifier: RunNotifier) {
        println("USpek is running....")
        super.run(notifier)
        println(testTree)
        testTree?.state = TestState.SUCCESS
        createDescriptions(testTree!!, suite, notifier)
    }

    private fun createDescriptions(testBranch: TestTree, parentDescription: Description, notifier: RunNotifier): Description {
        val description = if (testBranch.subtests.isNotEmpty()) {
            Description.createSuiteDescription(testBranch.name, UUID.randomUUID().toString())
        } else {
            Description.createTestDescription(testClass.simpleName, testBranch.name)
        }
        testBranch.subtests.forEach {
            val child = createDescriptions(it, description, notifier)
            parentDescription.addChild(child)
            if (child.isTest) {
                println("start: ${child.displayName}")
                notifier.fireTestStarted(description)
                when (it.state) {
                    USpekJUnitRunner.TestState.SUCCESS -> notifier.fireTestFinished(description)
                    else -> notifier.fireTestFailure(Failure(description, it.failureCause))
                }
            }
        }
        return description
    }

}