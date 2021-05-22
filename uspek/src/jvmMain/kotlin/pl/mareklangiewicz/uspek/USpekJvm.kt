package pl.mareklangiewicz.uspek

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

typealias StackTrace = Array<StackTraceElement>

actual val USpekTree?.location get() = this?.end?.stackTrace?.userCall?.location

actual val Throwable.causeLocation: CodeLocation?
    get() {
        val file = stackTrace.getOrNull(1)?.fileName
        val frame = cause?.stackTrace?.find { it.fileName == file }
        return frame?.location
    }

private val StackTraceElement.location get() = fileName?.let { CodeLocation(it, lineNumber) }

private val StackTrace.userCall get() = findUserCall()?.let(::getOrNull)

private fun StackTrace.findUserCall() = (1 until size).find {
    this[it - 1].fileName == "USpek.kt" && this[it].fileName != "USpek.kt"
}

fun suspekBlocking(ucontext: USpekContext = USpekContext(), code: suspend () -> Unit): USpekTree =
    runBlocking(ucontext) { suspek(code); ucontext.root }

fun CoroutineScope.suspekLaunch(ucontext: USpekContext = USpekContext(), code: suspend () -> Unit) {
    launch(ucontext) { suspek(code) }
}

fun CoroutineScope.suspekAsync(ucontext: USpekContext = USpekContext(), code: suspend () -> Unit): Deferred<USpekTree> =
    async(ucontext) { suspek(code); ucontext.root }
