package mareklangiewicz.pl.uspek.loggers

import mareklangiewicz.pl.uspek.Report
import mareklangiewicz.pl.uspek.ULogger

class BroadcastingLogger(private val loggers: List<ULogger>) : ULogger {
    override fun invoke(report: Report) {
        loggers.forEach {
            it.invoke(report)
        }
    }
}