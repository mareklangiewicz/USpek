package playground

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test

// temporary workaround from: https://github.com/Kotlin/kotlinx.coroutines/issues/1996
val testScope = MainScope()
val testCoroutineContext: CoroutineContext = testScope.coroutineContext
fun runBlockingTest(block: suspend () -> Unit): dynamic = testScope.promise { block() }

class ExampleTest {
    @Test
    fun exampleTest() = runBlockingTest { lsoDelayMs = 5; example() }
}
