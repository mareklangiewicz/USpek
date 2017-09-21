package mareklangiewicz.pl.uspek

import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod

class USpekJUnitRunner(private val testClass: Class<Any>) : BlockJUnit4ClassRunner(testClass) {

    override fun runChild(method: FrameworkMethod, notifier: RunNotifier) {
        USpek.log = { report ->
            println("Report: $report")
            when (report) {
                is USpek.Report.Start -> notifier.fireTestStarted(newTestDescription(report))
                is USpek.Report.Success -> notifier.fireTestFinished(newTestDescription(report))
                is USpek.Report.Failure -> notifier.fireTestFailure(Failure(newTestDescription(report), report.cause))
            }
        }
        println("USpek is running ${method.name}")
        methodBlock(method).evaluate()
        super.runChild(method, notifier)
    }

    override fun run(notifier: RunNotifier) {
        println("USpek is running....")
        super.run(notifier)
    }

    private fun newTestDescription(report: USpek.Report) =
            Description.createTestDescription(testClass.simpleName, "")
}