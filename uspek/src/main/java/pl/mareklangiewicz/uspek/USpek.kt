package pl.mareklangiewicz.uspek

import org.junit.Assert
import pl.mareklangiewicz.uspek.TestState.*

object USpek {

    private val finishedTests: MutableMap<CodeLocation, Throwable> = mutableMapOf()

    var log: ULog = ::logToConsole

    fun uspek(name: String, code: () -> Unit) {
        finishedTests.clear()
        log(TestInfo(name = name, location = currentUserCodeLocation, state = STARTED))
        while (true) {
            try {
                code()
                return
            } catch (e: TestEnd) {
                val location = e.stackTrace[1].location
                finishedTests[location] = e
                log(TestInfo(
                        location = location,
                        state = if (e.cause === null) SUCCESS else FAILURE,
                        failureLocation = e.causeLocation,
                        failureCause = e.cause
                ))
            }
        }
    }

    infix fun String.o(code: () -> Unit) = currentUserCodeLocation in finishedTests || throw try {
        log(TestInfo(name = this, location = currentUserCodeLocation, state = STARTED))
        code()
        TestEnd()
    }
    catch (e: TestEnd) { e }
    catch (e: Throwable) { TestEnd(e) }


    infix fun <T> T.eq(expected: T) = Assert.assertEquals(expected, this)

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
}
