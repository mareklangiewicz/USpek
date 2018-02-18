package pl.mareklangiewicz.uspek

object USpek {

    private val finishedTests: MutableMap<TestTrace, TestEnd> = mutableMapOf()

    var log: ULog = ::logToConsole

    fun uspek(name: String, code: () -> Unit) {
        finishedTests.clear()
        log.start(name, currentStackTrace.testTrace)
        while (true) {
            try {
                code()
                return
            } catch (e: TestEnd) {
                val trace = e.stackTrace.testTrace
                finishedTests[trace] = e
                log.end(trace, e)
            }
        }
    }

    infix fun String.o(code: () -> Unit) = currentStackTrace.testTrace.let {
        it in finishedTests || throw try {
            log.start(this, it)
            code()
            TestEnd()
        }
        catch (e: TestEnd) { e }
        catch (e: Throwable) { TestEnd(e) }
    }
}
