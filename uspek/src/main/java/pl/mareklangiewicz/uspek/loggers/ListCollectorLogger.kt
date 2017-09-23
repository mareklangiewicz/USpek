package pl.mareklangiewicz.uspek.loggers

import pl.mareklangiewicz.uspek.Report
import pl.mareklangiewicz.uspek.ULogger

class ListCollectorLogger : ULogger {

    private val reports = mutableListOf<Report>()

    fun getReports(): List<Report> = reports

    override fun invoke(report: Report) {
        reports.add(report)
    }
}