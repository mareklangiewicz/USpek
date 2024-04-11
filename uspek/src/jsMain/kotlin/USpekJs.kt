package pl.mareklangiewicz.uspek

// TODO: Kotlin/JS implementation is probably blocked by KJS/IR not supporting SourceMaps yet.
//   track this: https://youtrack.jetbrains.com/issue/KT-46275/KJS-IR-Support-SourceMaps

// typealias StackTrace = Array<StackTraceElement>

actual val USpekTree?.location: CodeLocation? get() = null // TODO // this?.end?.stackTrace?.userCall?.location

actual val Throwable.causeLocation: CodeLocation?
  get() = null // TODO
//    {
//        val file = stackTrace.getOrNull(1)?.fileName
//        val frame = cause?.stackTrace?.find { it.fileName == file }
//        return frame?.location
//    }

// private val StackTraceElement.location get() = fileName?.let { CodeLocation(it, lineNumber) }
//
// private val StackTrace.userCall get() = findUserCall()?.let(::getOrNull)
//
// private fun StackTrace.findUserCall() = (1 until size).find {
//    this[it - 1].fileName == "USpek.kt" && this[it].fileName != "USpek.kt"
//}
