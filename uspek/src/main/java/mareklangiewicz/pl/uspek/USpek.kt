package mareklangiewicz.pl.uspek

import org.junit.Assert

infix fun <T> T.eq(actual: T) = Assert.assertEquals(actual, this)

open class TestFinished(cause: Throwable? = null) : RuntimeException(cause)
class TestSuccess : TestFinished()
class TestFailure(cause: Throwable) : TestFinished(cause)

val visited: MutableMap<String, Throwable> = mutableMapOf()

fun uspek(name: String, code: () -> Unit) {
    visited.clear()
    println("USpek $name")
    var again = true
    do {
        try {
            code()
            again = false
        } catch (e: TestFinished) {
            val location = e.stackTrace[1].location
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
    return st[3].location in visited
}

val StackTraceElement.location get() = "$fileName:$lineNumber"

val Throwable.causeLocation: String
    get() {
        val file = stackTrace.getOrNull(1)?.fileName
        val frame = cause?.stackTrace?.find { it.fileName == file }
        return frame?.location ?: "UNKNOWN LOCATION"
    }

