package pl.mareklangiewicz.uspek

object USpek {

    private val finishedTests: MutableMap<CodeLocation, Throwable> = mutableMapOf()

    var log: ULog = ::logToConsole

    fun uspek(name: String, code: () -> Unit) {
        finishedTests.clear()
        log.start(name, currentUserCodeLocation)
        while (true) {
            try {
                code()
                return
            } catch (e: TestEnd) {
                val location = e.stackTrace[1].location
                finishedTests[location] = e
                log.end(location, e)
            }
        }
    }

    infix fun String.o(code: () -> Unit) = currentUserCodeLocation in finishedTests || throw try {
        log.start(this, currentUserCodeLocation)
        code()
        TestEnd()
    }
    catch (e: TestEnd) { e }
    catch (e: Throwable) { TestEnd(e) }
}
