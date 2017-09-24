package pl.mareklangiewicz.uspek


internal fun logToAll(vararg log: ULog) = fun(report: Report) = log.forEach { it(report) }

internal fun logToConsole(report: Report) = report.run {
    when (this) {
        is Report.Failure -> {
            println("FAILURE.($testLocation)")
            println("BECAUSE.($assertionLocation)")
            println(cause)
        }
        is Report.Success -> println("SUCCESS.($testLocation)")
        is Report.Start -> println(testName)
    }
}

internal fun logToList(reports: MutableList<Report>) = fun(report: Report) { reports.add(report) }


