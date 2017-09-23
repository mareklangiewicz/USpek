package mareklangiewicz.pl.uspek

class ListCollectorLogger : ULogger {

    private val reports = mutableListOf<Report>()

    fun getReports(): List<Report> = reports

    override fun invoke(report: Report) {
        reports.add(report)
    }
}