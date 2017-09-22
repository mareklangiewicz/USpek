package mareklangiewicz.pl.uspek

class CollectingLogger : ULogger {

    private val reports = mutableListOf<Report>()

    fun getReports(): List<Report> = reports

    override fun invoke(report: Report) {
        reports.add(report)
    }
}