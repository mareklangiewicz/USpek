package pl.mareklangiewicz.uspek

import kotlin.coroutines.*
import kotlin.time.*
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*

/**
 * Runs [suspek] inside [runTest] from kotlinx-coroutines-test API. See [runTest] for details.
 * (not the deprecated one - IntelliJ navigates to wrong runTest fun - scroll to the one above)
 *
 * @param context should contain USpekContext (so default is fine), or else it will REUSE [GlobalUSpekContext]!
 * (global one is delicate - have to make sure no other code is using it)
 *
 * @return [TestResult] WARNING: The only valid thing to do with a TestResult is to immediately return it from a test.
 * See [TestResult] kdoc for details.
 */
fun runTestUSpek(
  context: CoroutineContext = USpekContext(),
  timeout: Duration = 10.seconds,
  code: suspend TestScope.() -> Unit,
) = runTest(context, timeout) { suspek { code() } }

/**
 * Launches [suspek] inside new coroutine, without blocking current thread.
 *
 * @param context (or [this]) should contain USpekContext (so default is fine), or else it will REUSE [GlobalUSpekContext]!
 * (global one is delicate - have to make sure no other code is using it)
 */
fun CoroutineScope.launchUSpek(context: CoroutineContext = USpekContext(), code: suspend () -> Unit): Job =
  launch(context) { suspek(code) }

/**
 * Creates a coroutine running [suspek] in the background and returns its future result [USpekTree] as [Deferred].
 *
 * @param context (or [this]) should contain USpekContext (so default is fine), or else it will REUSE [GlobalUSpekContext]!
 * (global one is delicate - have to make sure no other code is using it)
 */
fun CoroutineScope.asyncUSpek(
  context: CoroutineContext = USpekContext(),
  code: suspend () -> Unit,
): Deferred<USpekTree> =
  async(context) { suspek(code); context.ucontext.root }
