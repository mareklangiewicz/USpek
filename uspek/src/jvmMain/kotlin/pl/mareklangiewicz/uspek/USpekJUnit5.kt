package pl.mareklangiewicz.uspek

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import java.net.URI

fun suspekTestFactory(code: suspend () -> Unit): DynamicNode = runBlocking {
    suspek(code)
    coroutineContext.ucontext.root.dnode
}

fun uspekTestFactory(code: () -> Unit): DynamicNode {
    uspek(code)
    return GlobalUSpekContext.root.dnode
}

private val USpekTree.dnode: DynamicNode get() =
    if (branches.isEmpty()) dtest
    else dynamicContainer(name, branches.values.map { it.dnode } + dtest)

private val USpekTree.dtest get() = dynamicTest(name) {
    println(status)
    end?.cause?.let { throw it }
}

// TODO: use JUnit5 URIs: https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests-uri-test-source
//   to be able to click (or F4) on the test in the Intellij test tree and to be navigated to exact test line
// TODO: check why this doesn't do the trick (or similar for dynamicContainer):
//   dynamicTest(name, location?.tsource) { end?.cause?.let { throw it } }
private val CodeLocation.tsource get() = URI("classpath:/$fileName?line=$lineNumber")

