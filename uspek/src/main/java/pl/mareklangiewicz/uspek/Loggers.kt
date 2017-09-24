package pl.mareklangiewicz.uspek


internal fun logToAll(vararg log: ULog) = fun(report: Report) = log.forEach { it(report) }

internal fun logToConsole(report: Report) = when (report) {
    is Report.Failure -> {
        println("FAILURE${report.testLocation}")
        println("BECAUSE${report.assertionLocation}")
        println("${report.cause}")
    }
    is Report.Success -> println("SUCCESS${report.testLocation}")
    is Report.Start -> println("START ${report.testName}")
}

internal fun logToList(reports: MutableList<Report>) = fun(report: Report) { reports.add(report) }


