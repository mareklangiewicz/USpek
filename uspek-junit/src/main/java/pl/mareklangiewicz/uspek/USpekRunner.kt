package pl.mareklangiewicz.uspek

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.Description
import org.junit.runner.Description.createSuiteDescription
import org.junit.runner.Description.createTestDescription
import org.junit.runner.Runner
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import java.util.Locale
import java.util.UUID.randomUUID

/**
 * This runner always runs all tests methods from given testClass
 */
class USpekRunner(testClass: Class<*>) : Runner() {

    private val context = GlobalUSpekContext

    private val description = createSuiteDescription(testClass.simpleName, randomUUID()).apply {
        context.root.branches.clear()
        val instance = testClass.newInstance()
        testClass.declaredMethods
                .filter { it.getAnnotation(Test::class.java) !== null }
                .forEach { it.invoke(instance) }
        addChild(context.root.description(testClass.name))
    }

    override fun getDescription(): Description = description
    override fun run(notifier: RunNotifier) = context.root.run(context.root.name, notifier)
}

private fun USpekTree.description(suite: String): Description {
    val description =
        if (branches.isEmpty()) createTestDescription(suite, name)
        else createSuiteDescription(name, randomUUID())
    branches.values.forEach { description.addChild(it.description("$suite.$name")) }
    data = description
    return description
}

private fun USpekTree.run(name: String, notifier: RunNotifier) {
    if (branches.isEmpty()) {
        val description = data as? Description
        notifier.fireTestStarted(description)
        println(status)
        when {
            failed -> {
                notifier.fireTestFailure(Failure(description, end?.cause))
                notifier.fireTestFinished(description)
            }
            finished -> notifier.fireTestFinished(description)
            else -> throw IllegalStateException("USpekTree branch not finished")
        }
    }
    else branches.values.forEach { it.run(name + "." + it.name, notifier) }
}

fun suspekBlocking(code: suspend () -> Unit) = runBlocking { suspek(code) }

fun CoroutineScope.suspekAsync(code: suspend () -> Unit) =
    async(USpekContext()) { suspek(code); coroutineContext.ucontext.root }

/** micro debugging ;-) */
val String.ud get() =
    println("ud [${Thread.currentThread().name.padEnd(40).substring(0, 40)}] [${getCurrentTimeString()}] $this")

val ud get() = "".ud

private fun getCurrentTimeString() = System.currentTimeMillis().let { String.format(Locale.US, "%tT:%tL", it, it) }