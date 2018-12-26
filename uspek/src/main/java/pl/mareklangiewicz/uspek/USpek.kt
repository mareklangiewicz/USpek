package pl.mareklangiewicz.uspek

import org.junit.Assert

fun uspek(code: () -> Unit) {
    while (true) try {
        context.branch = context.root
        code()
        return
    } catch (e: End) {
        context.branch.end = e
        log(context.branch)
    }
}

infix fun String.o(code: () -> Unit) {
    val branch = context.branch.branches[this] ?: Tree(this)
    branch.end === null || return
    context.branch.branches[this] = branch
    context.branch = branch
    log(branch)
    throw try { code(); End() }
    catch (e: End) { e }
    catch (e: Throwable) { End(e) }
}

infix fun String.ox(code: () -> Unit) = Unit

val context = Context()

data class Context(
    val root: Tree = Tree("uspek"),
    var branch: Tree = root
)

data class Tree(
    val name: String,
    val branches: MutableMap<String, Tree> = mutableMapOf(),
    var end: End? = null,
    var data: Any? = null
)

class End(cause: Throwable? = null) : RuntimeException(cause)

var log: (Tree) -> Unit = { println(it.status) }

val Tree.status get() = when {
        failed -> "FAILURE.($location)\nBECAUSE.($causeLocation)\n"
        finished -> "SUCCESS.($location)\n"
        else -> name
    }

val Tree.finished get() = end !== null

val Tree.failed get() = end?.cause !== null

val Tree?.location get() = this?.end?.stackTrace?.testTrace?.get(0)?.location

val Tree?.causeLocation get() = this?.end?.causeLocation

typealias StackTrace = Array<StackTraceElement>

infix fun <T> T.eq(expected: T) = Assert.assertEquals(expected, this)


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

typealias TestTrace = List<StackTraceElement>

val StackTrace.testTrace: TestTrace get() = slice(findUserCall()!!..findUserCall("uspek")!!)

private fun StackTrace.findUserCall(uSpekFun: String? = null) = (1 until size).find {
    uSpekFun in listOf(null, this[it - 1].methodName)
        && this[it - 1].fileName == "USpek.kt"
        && this[it].fileName != "USpek.kt"
}
