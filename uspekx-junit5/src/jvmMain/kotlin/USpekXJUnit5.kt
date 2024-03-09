package pl.mareklangiewicz.uspek

import kotlinx.coroutines.test.*
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import java.net.URI
import kotlin.coroutines.*
import kotlin.time.*
import kotlin.time.Duration.Companion.seconds

fun uspekTestFactory(code: () -> Unit): DynamicNode {
    uspek(code)
    return GlobalUSpekContext.root.dynamicNode()
}

@Deprecated("Use runTestUSpekJUnit5Factory")
fun suspekTestFactory(ucontext: USpekContext = USpekContext(), code: suspend () -> Unit): DynamicNode =
    runBlockingUSpek(ucontext, code).dynamicNode()

fun runTestUSpekJUnit5Factory(
    context: CoroutineContext = USpekContext(),
    timeout: Duration = 10.seconds,
    code: suspend TestScope.() -> Unit
): DynamicNode {
    runTestUSpek(context, timeout, code) // not returning TestResult is fine here - we are on JVM (not multiplatform)
    return context.ucontext.root.dynamicNode()
}

private fun USpekTree.dynamicNode(): DynamicNode =
    if (branches.isEmpty()) dynamicTest()
    else DynamicContainer.dynamicContainer(name, branches.values.map { it.dynamicNode() } + dynamicTest())

private fun USpekTree.dynamicTest(): DynamicTest =
    DynamicTest.dynamicTest(name.takeIf { it.isNotBlank() } ?: "NONAME") {
        println(status)
        end?.cause?.let { throw it }
    }

// TODO: use JUnit5 URIs: https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests-uri-test-source
//   to be able to click (or F4) on the test in the Intellij test tree and to be navigated to exact test line
// TODO: check why this doesn't do the trick (or similar for dynamicContainer):
//   dynamicTest(name, location?.tsource) { end?.cause?.let { throw it } }
private fun CodeLocation.testSource() = URI("classpath:/$fileName?line=$lineNumber")


