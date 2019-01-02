package pl.mareklangiewicz.uspek

fun uspek(code: () -> Unit) {
    while (true) try {
        uspekContext.branch = uspekContext.root
        code()
        return
    } catch (e: USpekException) {
        uspekContext.branch.end = e
        uspekLogger(uspekContext.branch)
    }
}

infix fun String.o(code: () -> Unit) {
    val branch = uspekContext.branch.branches[this] ?: USpekTree(this)
    branch.end === null || return
    uspekContext.branch.branches[this] = branch
    uspekContext.branch = branch
    uspekLogger(branch)
    throw try { code(); USpekException() }
    catch (e: USpekException) { e }
    catch (e: Throwable) { USpekException(e) }
}

@Suppress("UNUSED_PARAMETER")
infix fun String.ox(code: () -> Unit) = Unit

val uspekContext = USpekContext()

data class USpekContext(
    val root: USpekTree = USpekTree("uspek"),
    var branch: USpekTree = root
)

data class USpekTree(
    val name: String,
    val branches: MutableMap<String, USpekTree> = mutableMapOf(),
    var end: USpekException? = null,
    var data: Any? = null
)

class USpekException(cause: Throwable? = null) : RuntimeException(cause)

var uspekLogger: (USpekTree) -> Unit = { println(it.status) }

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

val StackTrace.uspekTrace: USpekTrace get() = slice(findUserCall()!!..findUserCall("uspek")!!)

private fun StackTrace.findUserCall(uSpekFun: String? = null) = (1 until size).find {
    uSpekFun in listOf(null, this[it - 1].methodName)
        && this[it - 1].fileName == "USpek.kt"
        && this[it].fileName != "USpek.kt"
}
