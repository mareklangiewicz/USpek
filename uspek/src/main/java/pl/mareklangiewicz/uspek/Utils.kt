package pl.mareklangiewicz.uspek

import org.junit.Assert

infix fun <T> T.eq(expected: T) = Assert.assertEquals(expected, this)

val currentUserCodeLocation get() = Thread.currentThread().stackTrace.userCodeLocation

val StackTraceElement.location get() = CodeLocation(fileName, lineNumber)

val Throwable.causeLocation: CodeLocation? get() {
    val file = stackTrace.getOrNull(1)?.fileName
    val frame = cause?.stackTrace?.find { it.fileName == file }
    return frame?.location
}

val Array<StackTraceElement>.userCodeLocation: CodeLocation get() {
    var atUSpekCode = false
    for (frame in this) {
        if (frame.fileName == "USpek.kt") {
            atUSpekCode = true
            continue
        }
        atUSpekCode && return frame.location
    }
    throw IllegalStateException("User code location not found")
}
