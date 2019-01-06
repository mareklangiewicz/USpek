package pl.mareklangiewicz.uspek

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

suspend fun suspek(code: suspend () -> Unit) = coroutineContext.ucontext.uspek { code() }

fun uspek(code: () -> Unit) = GlobalUSpekContext.uspek(code)

private inline fun USpekContext.uspek(code: () -> Unit) {
    while (true) try {
        branch = root
        code()
        break
    } catch (e: USpekException) {
        branch.end = e
        uspekLog(branch)
    }
}

suspend infix fun String.so(code: suspend () -> Unit): Unit = coroutineContext.ucontext.o(this) { code() }

infix fun String.o(code: () -> Unit) = GlobalUSpekContext.o(this, code)

private inline fun USpekContext.o(name: String, code: () -> Unit) {
    val subbranch = branch.branches.getOrPut(name) { USpekTree(name) }
    subbranch.end === null || return // already tested so skip this whole subbranch
    branch = subbranch // step through the tree into the subbranch
    uspekLog(subbranch)
    throw try { code(); USpekException() }
    catch (e: USpekException) { e }
    catch (e: Throwable) { USpekException(e) }
}

@Suppress("UNUSED_PARAMETER")
@Deprecated("Enable this test code", ReplaceWith("o(code)"))
infix fun String.ox(code: () -> Unit) = Unit

@Suppress("UNUSED_PARAMETER")
@Deprecated("Enable this test code", ReplaceWith("so(code)"))
infix fun String.sox(code: suspend () -> Unit) = Unit

data class USpekContext(
    val root: USpekTree = USpekTree("uspek"),
    var branch: USpekTree = root
) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<USpekContext> = Key
    companion object Key : CoroutineContext.Key<USpekContext>
}

val GlobalUSpekContext = USpekContext()

val CoroutineContext.ucontext get() = this[USpekContext] ?: GlobalUSpekContext

data class USpekTree(
    val name: String,
    val branches: MutableMap<String, USpekTree> = mutableMapOf(),
    var end: USpekException? = null,
    var data: Any? = null
)

class USpekException(cause: Throwable? = null) : RuntimeException(cause)

var uspekLog: (USpekTree) -> Unit = { println(it.status) }

val USpekTree.status get() = when {
        failed -> "FAILURE.($location)\nBECAUSE.($causeLocation)\n"
        finished -> "SUCCESS.($location)\n"
        else -> name
    }

val USpekTree.finished get() = end !== null

val USpekTree.failed get() = end?.cause !== null

val USpekTree?.location get() = this?.end?.stackTrace?.uspekTrace?.get(0)?.location

val USpekTree?.causeLocation get() = this?.end?.causeLocation

typealias StackTrace = Array<StackTraceElement>

infix fun <T> T.eq(expected: T) = assert(this == expected)


data class CodeLocation(val fileName: String, val lineNumber: Int) {
    override fun toString() = "$fileName:$lineNumber"
}

val StackTraceElement.location get() = CodeLocation(fileName, lineNumber)

val Throwable.causeLocation: CodeLocation?
    get() {
        val file = stackTrace.getOrNull(1)?.fileName
        val frame = cause?.stackTrace?.find { it.fileName == file }
        return frame?.location
    }

typealias USpekTrace = List<StackTraceElement>

val StackTrace.uspekTrace: USpekTrace? get() {
//    logTrace()
    val from = findUserCall() ?: return null
    val to = findUserCall("uspek") ?: return null
    val ut = slice(from..to)
//    ut.logTrace()
    return ut
}

// TODO: remove after checking how stack traces are changing after suspensions (like delay etc)
fun StackTrace.logTrace() = toList().logTrace()

fun USpekTrace.logTrace() {
    for (elem in this) {
        println(elem)
    }
}

private fun StackTrace.findUserCall(uSpekFun: String? = null) = (1 until size).find {
    uSpekFun in listOf(null, this[it - 1].methodName)
        && this[it - 1].fileName == "USpek.kt"
        && this[it].fileName != "USpek.kt"
}
