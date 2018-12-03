package pl.mareklangiewicz.uspek

import org.junit.Assert

typealias StackTrace = Array<StackTraceElement>

infix fun <T> T.eq(expected: T) = Assert.assertEquals(expected, this)


data class CodeLocation(val fileName: String, val lineNumber: Int) {
    override fun toString() = "$fileName:$lineNumber"
}

val StackTraceElement.location get() = CodeLocation(fileName, lineNumber)

val Throwable.causeLocation: CodeLocation?
    get() {
        val file = stackTrace.getOrNull(1)?.fileName
        val frame = cause?.stackTrace?.find { it.fileName == file }
        return frame?.location
    }

typealias TestTrace = List<StackTraceElement>

val StackTrace.testTrace: TestTrace get() = slice(findUserCall()!!..findUserCall("uspek")!!)

private fun StackTrace.findUserCall(uSpekFun: String? = null) = (1 until size).find {
            uSpekFun in listOf(null, this[it - 1].methodName)
            && this[it - 1].fileName == "USpek.kt"
            && this[it].fileName != "USpek.kt"
}
