package mareklangiewicz.pl.uspek

import org.junit.Assert

object USpek {

    private val finishedTests: MutableMap<String, Throwable> = mutableMapOf()

    var log: (String) -> Unit = { println(it) }

    fun uspek(name: String, rethrow: Boolean = false, code: () -> Unit) {
        finishedTests.clear()
        log("USpek $name")
        var again = true
        do {
            try {
                code()
                again = false
            } catch (e: TestFinished) {
                val location = e.stackTrace[1].location
                finishedTests[location] = e
                val ok = e is TestSuccess
                val prefix = if (ok) "SUCCESS" else "FAILURE"
                val postfix = if (ok) "" else "!#!#!#!#!#!#!#!#!#!#!"
                log("$prefix.($location)$postfix")
                if (!ok) {
                    log("BECAUSE.(${e.causeLocation})")
                    log(e.cause.toString())
                }
            }
        } while (again)
        if (rethrow)
            finishedTests.values.find { it is TestFailure }?.let { throw it.cause ?: it }
    }

    infix fun String.o(code: () -> Unit) = itIsFinished || throw try {
        log(this)
        code()
        TestSuccess()
    }
    catch (e: TestSuccess) { e }
    catch (e: TestFailure) { e }
    catch (e: Throwable) { TestFailure(e) }

    infix fun <T> T.eq(actual: T) = Assert.assertEquals(actual, this)

    private open class TestFinished(cause: Throwable? = null) : RuntimeException(cause)
    private class TestSuccess : TestFinished()
    private class TestFailure(cause: Throwable) : TestFinished(cause)

    private val itIsFinished get() = Thread.currentThread().stackTrace.userCodeLocation in finishedTests

    private val StackTraceElement.location get() = "$fileName:$lineNumber"

    private val Throwable.causeLocation: String?
        get() {
            val file = stackTrace.getOrNull(1)?.fileName
            val frame = cause?.stackTrace?.find { it.fileName == file }
            return frame?.location
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

