package pl.mareklangiewicz.uspek

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import pl.mareklangiewicz.uspek.TestState.*
import pl.mareklangiewicz.uspek.data.*

class USpekTest {

    private val infos = mutableListOf<TestInfo>()

    object Report { // temporary wrapper for tests
        fun Start(name: String, location: CodeLocation) = TestInfo(name, location, state = STARTED)
        fun Success(testLocation: CodeLocation) = TestInfo(location = testLocation, state = SUCCESS)
        fun Failure(testLocation: CodeLocation, assertionLocation: CodeLocation, cause: Throwable)
                = TestInfo(location = testLocation, state = FAILURE, failureLocation = assertionLocation, failureCause = cause)
    }

    @Before
    fun setUp() {
        USpek.log = logToList(infos)
    }

    @Test
    fun `should create start report at the beginning of uspek`() {
        uspek_test_1()
        assertThat(infos).isEqualTo(listOf(Report.Start("some test", CodeLocation("uspek_test_1.kt", 6))))
    }

    @Test
    fun `should create start report at the beginning of nested test`() {
        uspek_test_2()
        assertThat(infos).contains(Report.Start("some nested test", CodeLocation("uspek_test_2.kt", 8)))
    }

    @Test
    fun `should create success report after finishing test with success`() {
        uspek_test_3()
        assertThat(infos).contains(Report.Success(testLocation = CodeLocation("uspek_test_3.kt", 9)))
    }

    @Test
    fun `should create failure report after finishing test with error`() {
        uspek_test_4()
        val actual = infos.filter { it.state == FAILURE }
        val expected = Report.Failure(
                testLocation = CodeLocation("uspek_test_4.kt", 9),
                assertionLocation = CodeLocation("uspek_test_4.kt", 10),
                cause = actual[0].failureCause!!)
        assertThat(actual).contains(expected)
    }

    @Test
    fun `should start all outer clauses in proper order`() {
        uspek_test_5()
        assertThat(infos)
                .containsSequence(listOf(
                        Report.Start("some test", CodeLocation("uspek_test_5.kt", 8)),
                        Report.Start("some nested test", CodeLocation("uspek_test_5.kt", 9))))
    }

    @Test
    fun `should start all nested tests`() {
        uspek_test_6()
        assertThat(infos)
                .containsAll(listOf(
                        Report.Start("first test", CodeLocation("uspek_test_6.kt", 8)),
                        Report.Start("second test", CodeLocation("uspek_test_6.kt", 11))))
    }

    @Test
    fun `should gather success from all nested tests`() {
        uspek_test_7()
        assertThat(infos)
                .containsAll(listOf(
                        Report.Success(testLocation = CodeLocation("uspek_test_7.kt", lineNumber = 9)),
                        Report.Success(testLocation = CodeLocation("uspek_test_7.kt", lineNumber = 13))))
    }

    @Test
    fun `should gather failures from all nested tests`() {
        uspek_test_8()
        val actual = infos.filter { it.state == FAILURE }
        val expected = listOf(
                Report.Failure(
                        testLocation = CodeLocation("uspek_test_8.kt", lineNumber = 9),
                        assertionLocation = CodeLocation("uspek_test_8.kt", lineNumber = 10),
                        cause = actual[0].failureCause!!),
                Report.Failure(
                        testLocation = CodeLocation("uspek_test_8.kt", lineNumber = 13),
                        assertionLocation = CodeLocation("uspek_test_8.kt", lineNumber = 14),
                        cause = actual[1].failureCause!!))
        assertThat(actual).containsSequence(expected)
    }

    @Test
    fun `should gather all failures along with successes`() {
        uspek_test_9()
        val expected = listOf(
                Report.Start("some test", CodeLocation("uspek_test_9.kt", 8)),
                Report.Start("first test", CodeLocation("uspek_test_9.kt", 9)),
                Report.Failure(
                        testLocation = CodeLocation("uspek_test_9.kt", lineNumber = 9),
                        assertionLocation = CodeLocation("uspek_test_9.kt", lineNumber = 10),
                        cause = infos[2].failureCause!!),
                Report.Start("second test", CodeLocation("uspek_test_9.kt", 13)),
                Report.Success(
                        testLocation = CodeLocation("uspek_test_9.kt", lineNumber = 13)))
        assertThat(infos).isEqualTo(expected)
    }

    @Test
    fun `should execute tests which are nested multiple times`() {
        uspek_test_10()
        assertThat(infos)
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

