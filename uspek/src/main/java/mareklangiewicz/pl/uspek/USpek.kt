package mareklangiewicz.pl.uspek

import org.junit.Assert

object USpek {

    private val finishedTests: MutableMap<CodeLocation, Throwable> = mutableMapOf()

    var log: (Report) -> Unit = ::defaultLogger

    fun uspek(name: String, code: () -> Unit) {
        finishedTests.clear()
        log(Report.Start(name, currentUserCodeLocation))
        while(true) {
            try {
                code()
                return
            } catch (e: TestFinished) {
                val location = e.stackTrace[1].location
                finishedTests[location] = e
                if (e is TestSuccess) {
                    log(Report.Success(location))
                } else {
                    log(Report.Failure(location, e.causeLocation, e.cause))
                }
            }
        }
    }

    infix fun String.o(code: () -> Unit) = currentUserCodeLocation in finishedTests || throw try {
        log(Report.Start(this, currentUserCodeLocation))
        code()
        TestSuccess()
    } catch (e: TestFinished) {
        e
    } catch (e: Throwable) {
        TestFailure(e)
    }

    infix fun <T> T.eq(expected: T) = Assert.assertEquals(expected, this)

    private open class TestFinished(cause: Throwable? = null) : RuntimeException(cause)
    private class TestSuccess : TestFinished()
    private class TestFailure(cause: Throwable) : TestFinished(cause)

    private val currentUserCodeLocation get() = Thread.currentThread().stackTrace.userCodeLocation

    private val StackTraceElement.location get() = CodeLocation(fileName, lineNumber)

    private val Throwable.causeLocation: CodeLocation?
        get() {
            val file = stackTrace.getOrNull(1)?.fileName
            val frame = cause?.stackTrace?.find { it.fileName == file }
            return frame?.location
        }

    private val Array<StackTraceElement>.userCodeLocation: CodeLocation
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

    data class CodeLocation(val fileName: String, val lineNumber: Int) {
        override fun toString() = "($fileName:$lineNumber)"
    }

    sealed class Report {
        abstract val testLocation: CodeLocation

        data class Failure(override val testLocation: CodeLocation,
                           val assertionLocation: CodeLocation?,
                           val cause: Throwable?) : Report()

        data class Success(override val testLocation: CodeLocation) : Report()

        data class Start(val testName: String, override val testLocation: CodeLocation) : Report()
    }
}

