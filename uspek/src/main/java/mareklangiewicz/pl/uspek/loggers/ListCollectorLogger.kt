package mareklangiewicz.pl.uspek.loggers

import mareklangiewicz.pl.uspek.Report
import mareklangiewicz.pl.uspek.ULogger

class ListCollectorLogger : ULogger {

    private val reports = mutableListOf<Report>()

    fun getReports(): List<Report> = reports

    override fun invoke(report: Report) {
        reports.add(report)
    }
}