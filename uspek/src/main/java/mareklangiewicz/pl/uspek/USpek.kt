package mareklangiewicz.pl.uspek

import org.junit.Assert

object USpek {

    private val visited: MutableMap<String, Throwable> = mutableMapOf()

    fun uspek(name: String, rethrow: Boolean = false, code: () -> Unit) {
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
        if (rethrow)
            visited.values.find { it is TestFailure } ?.let { throw it.cause ?: it }
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

    infix fun <T> T.eq(actual: T) = Assert.assertEquals(actual, this)


    private open class TestFinished(cause: Throwable? = null) : RuntimeException(cause)
    private class TestSuccess : TestFinished()
    private class TestFailure(cause: Throwable) : TestFinished(cause)

    private fun finished(): Boolean {
        val st = Thread.currentThread().stackTrace
        return st.userCodeLocation in visited
    }

    private val StackTraceElement.location get() = "$fileName:$lineNumber"

    private val Throwable.causeLocation: String
        get() {
            val file = stackTrace.getOrNull(1)?.fileName
            val frame = cause?.stackTrace?.find { it.fileName == file }
            return frame?.location ?: throw IllegalStateException("Exception cause code location not found")
        }

    private val Array<StackTraceElement>.userCodeLocation: String
        get() {
            var atUSpekCode = false
            for (frame in this) {
                if (frame.fileName == "USpek.kt") {
                    atUSpekCode = true
                    continue
                }
                if (atUSpekCode) {
                    return frame.location
                }
            }
            throw IllegalStateException("User code location not found")
        }
}

