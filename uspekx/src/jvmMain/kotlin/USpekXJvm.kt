package pl.mareklangiewicz.uspek

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun suspekBlocking(ucontext: USpekContext = USpekContext(), code: suspend () -> Unit): USpekTree =
    runBlocking(ucontext) { suspek(code); ucontext.root }

fun CoroutineScope.suspekLaunch(ucontext: USpekContext = USpekContext(), code: suspend () -> Unit) {
    launch(ucontext) { suspek(code) }
}

fun CoroutineScope.suspekAsync(ucontext: USpekContext = USpekContext(), code: suspend () -> Unit): Deferred<USpekTree> =
    async(ucontext) { suspek(code); ucontext.root }
