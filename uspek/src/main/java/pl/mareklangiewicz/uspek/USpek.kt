package pl.mareklangiewicz.uspek

object USpek {

    private val status: MutableMap<TestTrace, TestInfo> = mutableMapOf()

    var log: ULog = ::logToConsole

    var skip: (name: String, trace: TestTrace) -> Boolean = { _, trace -> status[trace]?.finished == true }

    fun uspek(name: String, code: () -> Unit) {
        status.clear()
        start(name, currentStackTrace.testTrace)
        while (true) {
            try { code(); return }
            catch (e: TestEnd) { end(e.stackTrace.testTrace, e) }
        }
    }

    infix fun String.o(code: () -> Unit) = currentStackTrace.testTrace.let {
        skip(this, it) || throw try {
            start(this, it)
            code()
            TestEnd()
        }
        catch (e: TestEnd) { e }
        catch (e: Throwable) { TestEnd(e) }
    }

    infix fun String.ox(code: () -> Unit) = true

    private fun start(name: String, trace: TestTrace) {
        status[trace] = TestInfo(false, name, trace)
        log(status[trace]!!)
    }

    private fun end(trace: TestTrace, exception: TestEnd) {
        val info = TestInfo(
            finished = true,
            failureLocation = exception.causeLocation,
            failureCause = exception.cause
        )
        status[trace]!!.applyExistentFrom(info)
        log(status[trace]!!)
    }
}

