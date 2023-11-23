package pl.mareklangiewicz.uspek

// TODO: Try to use https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/stack-trace-to-string.html
//   to have more common implementation - parse it manually. But parse it carefully/defensively,
//   because the format is not stable.
//   On native (to have file locations and line numbers) it requires setting in gradle.properties:
//   kotlin.native.binary.sourceInfoType=libbacktrace
//   https://kotlinlang.org/docs/whatsnew1620.html#better-stack-traces-with-libbacktrace
//   (and running in debug mode - sth like: gradle runDebugExecutableLinuxX64)

//typealias StackTrace = Array<StackTraceElement>

actual val USpekTree?.location:CodeLocation? get() = null // TODO // this?.end?.stackTrace?.userCall?.location

actual val Throwable.causeLocation: CodeLocation?
    get() = null // TODO
//    {
//        val file = stackTrace.getOrNull(1)?.fileName
//        val frame = cause?.stackTrace?.find { it.fileName == file }
//        return frame?.location
//    }

//private val StackTraceElement.location get() = fileName?.let { CodeLocation(it, lineNumber) }
//
//private val StackTrace.userCall get() = findUserCall()?.let(::getOrNull)
//
//private fun StackTrace.findUserCall() = (1 until size).find {
//    this[it - 1].fileName == "USpek.kt" && this[it].fileName != "USpek.kt"
//}
