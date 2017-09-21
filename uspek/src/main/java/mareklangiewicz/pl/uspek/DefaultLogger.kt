package mareklangiewicz.pl.uspek

fun defaultLogger(report: USpek.Report) {
    when (report) {
        is USpek.Report.Failure -> {
            println("FAILURE${report.testLocation}!#!#!#!#!#!#!#!#!#!#!")
            println("BECAUSE${report.assertionLocation}")
            println("${report.cause}")
        }
        is USpek.Report.Success -> println("SUCCESS${report.testLocation}")
        is USpek.Report.Start -> println("START ${report.testName}")
    }
}