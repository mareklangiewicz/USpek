package mareklangiewicz.pl.uspek.loggers

import mareklangiewicz.pl.uspek.Report

fun consoleOutputLogger(report: Report) {
    when (report) {
        is Report.Failure -> {
            println("FAILURE${report.testLocation}")
            println("BECAUSE${report.assertionLocation}")
            println("${report.cause}")
        }
        is Report.Success -> println("SUCCESS${report.testLocation}")
        is Report.Start -> println("START ${report.testName}")
    }
}