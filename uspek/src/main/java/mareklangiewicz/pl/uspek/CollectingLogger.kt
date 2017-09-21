package mareklangiewicz.pl.uspek

class CollectingLogger : (USpek.Report) -> Unit {

    private val reports = mutableListOf<USpek.Report>()

    fun getReports(): List<USpek.Report> = reports

    override fun invoke(report: USpek.Report) {
        reports.add(report)
    }
}