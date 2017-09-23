package pl.mareklangiewicz.uspek

import pl.mareklangiewicz.uspek.data.*
import pl.mareklangiewicz.uspek.loggers.ListCollectorLogger
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class USpekTest {

    private val collectingLogger = ListCollectorLogger()

    @Before
    fun setUp() {
        USpek.log = collectingLogger
    }

    @Test
    fun `should create start report at the beginning of uspek`() {
        uspek_test_1()
        assertThat(collectingLogger.getReports())
                .isEqualTo(listOf(Report.Start("some test", CodeLocation("uspek_test_1.kt", 6))))
    }

    @Test
    fun `should create start report at the beginning of nested test`() {
        uspek_test_2()
        assertThat(collectingLogger.getReports())
                .contains(Report.Start("some nested test", CodeLocation("uspek_test_2.kt", 8)))
    }

    @Test
    fun `should create success report after finishing test with success`() {
        uspek_test_3()
        assertThat(collectingLogger.getReports())
                .contains(Report.Success(testLocation = CodeLocation("uspek_test_3.kt", 9)))
    }

    @Test
    fun `should create failure report after finishing test with error`() {
        uspek_test_4()
        assertThat(collectingLogger.getReports().filterIsInstance<Report.Failure>())
                .usingElementComparator(FailureReportComparator)
                .contains(Report.Failure(
                        testLocation = CodeLocation("uspek_test_4.kt", 9),
                        assertionLocation = CodeLocation("uspek_test_4.kt", 10),
                        cause = AssertionError()))
    }

    @Test
    fun `should start all outer clauses in proper order`() {
        uspek_test_5()
        assertThat(collectingLogger.getReports())
                .containsSequence(listOf(
                        Report.Start("some test", CodeLocation("uspek_test_5.kt", 8)),
                        Report.Start("some nested test", CodeLocation("uspek_test_5.kt", 9))))
    }

    @Test
    fun `should start all nested tests`() {
        uspek_test_6()
        assertThat(collectingLogger.getReports())
                .containsAll(listOf(
                        Report.Start("first test", CodeLocation("uspek_test_6.kt", 8)),
                        Report.Start("second test", CodeLocation("uspek_test_6.kt", 11))))
    }

    @Test
    fun `should gather success from all nested tests`() {
        uspek_test_7()
        assertThat(collectingLogger.getReports())
                .containsAll(listOf(
                        Report.Success(testLocation = CodeLocation("uspek_test_7.kt", lineNumber = 9)),
                        Report.Success(testLocation = CodeLocation("uspek_test_7.kt", lineNumber = 13))))
    }

    @Test
    fun `should gather failures from all nested tests`() {
        uspek_test_8()
        assertThat(collectingLogger.getReports().filterIsInstance<Report.Failure>())
                .usingElementComparator(FailureReportComparator)
                .containsSequence(listOf(
                        Report.Failure(
                                testLocation = CodeLocation("uspek_test_8.kt", lineNumber = 9),
                                assertionLocation = CodeLocation("uspek_test_8.kt", lineNumber = 10),
                                cause = AssertionError()),
                        Report.Failure(
                                testLocation = CodeLocation("uspek_test_8.kt", lineNumber = 13),
                                assertionLocation = CodeLocation("uspek_test_9.kt", lineNumber = 14),
                                cause = AssertionError())))
    }

    @Test
    fun `should gather all failures along with successes`() {
        uspek_test_9()
        assertThat(collectingLogger.getReports())
                .usingElementComparator(ReportElementComparator)
                .isEqualTo(listOf(
                        Report.Start("some test", CodeLocation("uspek_test_9.kt", 8)),
                        Report.Start("first test", CodeLocation("uspek_test_9.kt", 9)),
                        Report.Failure(
                                testLocation = CodeLocation("uspek_test_9.kt", lineNumber = 9),
                                assertionLocation = CodeLocation("uspek_test_9.kt", lineNumber = 10),
                                cause = AssertionError()),
                        Report.Start("second test", CodeLocation("uspek_test_9.kt", 13)),
                        Report.Success(
                                testLocation = CodeLocation("uspek_test_9.kt", lineNumber = 13))))
    }

    @Test
    fun `should execute tests which are nested multiple times`() {
        uspek_test_10()
        assertThat(collectingLogger.getReports())
                .usingElementComparator(ReportElementComparator)
                .containsSequence(listOf(Report.Start("some test", CodeLocation("uspek_test_10.kt", 8)),
                        Report.Start("first test", CodeLocation("uspek_test_10.kt", 9)),
                        Report.Start("second test", CodeLocation("uspek_test_10.kt", 12)),
                        Report.Success(
                                testLocation = CodeLocation("uspek_test_10.kt", lineNumber = 12)),
                        Report.Start("first test", CodeLocation("uspek_test_10.kt", 9)),
                        Report.Success(
                                testLocation = CodeLocation("uspek_test_10.kt", lineNumber = 9))
                ))
    }
}

private object ReportElementComparator : Comparator<Report> {
    override fun compare(o1: Report, o2: Report): Int {
        if (o1.javaClass != o2.javaClass) return -1
        return when (o1) {
            is Report.Start -> equalityCompare(o1, o2)
            is Report.Failure -> FailureReportComparator.compare(o1, o2 as Report.Failure)
            is Report.Success -> equalityCompare(o1, o2)
        }
    }
}

private object FailureReportComparator : Comparator<Report.Failure> {
    override fun compare(o1: Report.Failure, o2: Report.Failure): Int {
        return if (o1.testLocation == o2.testLocation
                && o1.testLocation == o2.testLocation
                && o1.cause?.javaClass?.equals(o2.cause?.javaClass) != false) 0 else -1
    }
}

private fun <T> equalityCompare(t1: T, t2: T) = Comparator<T> { o1, o2 -> if (o1 == o2) 0 else -1 }.compare(t1, t2)