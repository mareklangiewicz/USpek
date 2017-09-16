package pl.elpassion.eltc

import org.junit.Assert

infix fun <T> T.eq(actual: T) = Assert.assertEquals(this, actual)

typealias CodeLocation = String

open class TestFinished(cause: Throwable? = null) : RuntimeException(cause)
class TestSuccess : TestFinished()
class TestFailure(cause: Throwable) : TestFinished(cause)

val visited: MutableMap<CodeLocation, Throwable> = mutableMapOf()

fun uspek(name: String, code: () -> Unit) {
    visited.clear()
    println("USpek $name")
    var again = true
    do {
        try {
            code()
            again = false
        } catch (e: TestFinished) {
            val location = e.stackTrace[1].toCodeLocation()
            visited[location] = e
            val ok = e is TestSuccess
            val prefix = if (ok) "SUCCESS" else "FAILURE"
            val postfix = if (ok) "" else "!#!#!#!#!#!#!#!#!#!#!"
            println("$prefix.($location)$postfix")
            if (!ok) {
                println("BECAUSE.(${e.causeLocation})")
                println(e.cause)
            }
        }
    } while (again)
}

infix fun String.o(code: () -> Unit) {
    if (finished()) return
    println(this)
    try {
        code()
    } catch (e: TestSuccess) {
        throw e
    } catch (e: TestFailure) {
        throw e
    } catch (e: Throwable) {
        throw TestFailure(e)
    }
    throw TestSuccess()
}

fun finished(): Boolean {
    val st = Thread.currentThread().stackTrace
    return st[3].toCodeLocation() in visited
}

fun StackTraceElement.toCodeLocation(): CodeLocation = "$fileName:$lineNumber"

val Throwable.causeLocation: CodeLocation get() {
    val file = stackTrace.getOrNull(1)?.fileName
    val frame = cause?.stackTrace?.find { it.fileName == file }
    return frame?.toCodeLocation() ?: "UNKNOWN LOCATION"
}
