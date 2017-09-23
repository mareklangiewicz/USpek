package pl.mareklangiewicz.uspek.loggers

import pl.mareklangiewicz.uspek.Report
import pl.mareklangiewicz.uspek.ULogger

class BroadcastingLogger(private val loggers: List<ULogger>) : ULogger {
    override fun invoke(report: Report) {
        loggers.forEach {
            it.invoke(report)
        }
    }
}