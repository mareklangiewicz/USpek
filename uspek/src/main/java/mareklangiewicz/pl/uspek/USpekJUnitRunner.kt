package mareklangiewicz.pl.uspek

import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod

class USpekJUnitRunner(testClass: Class<Any>) : BlockJUnit4ClassRunner(testClass) {

    private val suite = Description.createSuiteDescription(testClass.simpleName)

    override fun runChild(method: FrameworkMethod, notifier: RunNotifier) {
        USpek.log = { report ->
            println("Report: $report")
            when (report) {
                is USpek.Report.Start -> {
                    val testDescription = newTestDescription(method, report.testName)
                    suite.children.first().addChild(testDescription)
                    notifier.fireTestStarted(testDescription)
                    notifier.fireTestFinished(testDescription)
                }
//                is USpek.Report.Success -> notifier.fireTestFinished(newTestDescription(method, report))
//                is USpek.Report.Failure -> notifier.fireTestFailure(Failure(newTestDescription(method, report), report.cause))
            }
        }
        println("USpek is running ${method.name}")
        suite.addChild(Description.createSuiteDescription(method.name))
        super.runChild(method, notifier)
    }

    override fun run(notifier: RunNotifier) {
        println("USpek is running....")
        super.run(notifier)
    }

    private fun newTestDescription(method: FrameworkMethod, name: String) =
            Description.createTestDescription(method.name, name)
}