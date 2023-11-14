package pl.mareklangiewicz.uspek

import kotlinx.coroutines.runBlocking
import kotlin.coroutines.*

/**
 * Runs [suspek] inside a new coroutine and **blocks** the current thread until its completion.
 * See [runBlocking] for more info how/when to use it. In most cases it's better to use [runTestUSpek].
 *
 * @param context should contain USpekContext (so default is fine), or else it will REUSE [GlobalUSpekContext]!
 * (global one is delicate - have to make sure no other code is using it)
 */
fun runBlockingUSpek(context: CoroutineContext = USpekContext(), code: suspend () -> Unit): USpekTree =
    runBlocking(context) { suspek(code); context.ucontext.root }

