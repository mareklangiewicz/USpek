package mareklangiewicz.pl.uspek

import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier
import java.util.*

class USpekJUnitRunner(testClass: Class<Any>) : Runner() {

    val testDescription = Description.createSuiteDescription("kasper", UUID.randomUUID().toString())
    val suit2 = Description.createSuiteDescription("sui2", UUID.randomUUID().toString())
    val suit3 = Description.createSuiteDescription("sui3", UUID.randomUUID().toString())
    val nested = Description.createTestDescription("nested", "description")
    val nested2 = Description.createTestDescription("nested", "description2")

    init {
        testDescription.addChild(nested)
        testDescription.addChild(nested2)
        suit2.addChild(testDescription)
        suit3.addChild(suit2)
    }

    override fun run(notifier: RunNotifier) {
        notifier.fireTestStarted(nested)
        notifier.fireTestFinished(nested)

        notifier.fireTestStarted(nested2)
        notifier.fireTestFinished(nested2)
    }

    override fun getDescription(): Description {
        return suit3
    }
}