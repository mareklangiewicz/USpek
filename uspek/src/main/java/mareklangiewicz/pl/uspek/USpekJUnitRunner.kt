package mareklangiewicz.pl.uspek

import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier
import java.util.*

class USpekJUnitRunner(testClass: Class<Any>) : Runner() {

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

    private val rootDescription = Description.createSuiteDescription(testClass.simpleName, UUID.randomUUID().toString())

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
        val instance = testClass.newInstance()
        testClass.declaredMethods.forEach { it.invoke(instance) }
        testTree?.state = TestState.SUCCESS
        rootDescription.addChild(createDescriptions(testTree!!, testClass.name))
    }

    override fun getDescription(): Description {
        return rootDescription
    }

    override fun run(notifier: RunNotifier) {
        println("USpek is running....")
    }

    private fun createDescriptions(testBranch: TestTree, testSuite: String): Description {
        val description = if (testBranch.subtests.isNotEmpty()) {
            Description.createSuiteDescription(testBranch.name, UUID.randomUUID().toString())
        } else {
            Description.createTestDescription(testSuite, testBranch.name)
        }
        testBranch.subtests.forEach {
            val child = createDescriptions(it, testSuite + "." + testBranch.name)
            description.addChild(child)
        }
        return description
    }

}