package pl.mareklangiewicz.uspek

fun uspek(code: () -> Unit) {
    while (true) try {
        uspekContext.branch = uspekContext.root
        code()
        break
    } catch (e: USpekException) {
        uspekContext.branch.end = e
        uspekLog(uspekContext.branch)
    }
}

infix fun String.o(code: () -> Unit) {
    val subbranch = uspekContext.branch.branches.getOrPut(this) { USpekTree(this) }
    subbranch.end === null || return // already tested so skip this whole subbranch
    uspekContext.branch = subbranch // step through the tree into the subbranch
    uspekLog(subbranch)
    throw try { code(); USpekException() }
    catch (e: USpekException) { e }
    catch (e: Throwable) { USpekException(e) }
}

@Suppress("UNUSED_PARAMETER")
@Deprecated("Enable this test code", ReplaceWith("o(code)"))
infix fun String.ox(code: () -> Unit) = Unit

data class USpekContext(
    val root: USpekTree = USpekTree("uspek"),
    var branch: USpekTree = root
)

val uspekContext = USpekContext()

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

val USpekTree?.location get() = this?.end?.stackTrace?.userCall?.location

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

val StackTrace.userCall get() = findUserCall()?.let(::getOrNull)

private fun StackTrace.findUserCall() = (1 until size).find {
    this[it - 1].fileName == "USpek.kt" && this[it].fileName != "USpek.kt"
}
