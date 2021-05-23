package pl.mareklangiewicz.uspek

typealias StackTrace = Array<StackTraceElement>

actual val USpekTree?.location get() = this?.end?.stackTrace?.userCall?.location

actual val Throwable.causeLocation: CodeLocation?
    get() {
        val file = stackTrace.getOrNull(1)?.fileName
        val frame = cause?.stackTrace?.find { it.fileName == file }
        return frame?.location
    }

private val StackTraceElement.location get() = fileName?.let { CodeLocation(it, lineNumber) }

private val StackTrace.userCall get() = findUserCall()?.let(::getOrNull)

private fun StackTrace.findUserCall() = (1 until size).find {
    this[it - 1].fileName == "USpek.kt" && this[it].fileName != "USpek.kt"
}
