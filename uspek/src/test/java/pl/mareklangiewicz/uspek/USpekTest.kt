package pl.mareklangiewicz.uspek

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import pl.mareklangiewicz.uspek.TestState.*
import pl.mareklangiewicz.uspek.data.*

class USpekTest {

    private val infos = mutableListOf<TestInfo>()

    @Before
    fun setUp() {
        USpek.log = logToList(infos)
    }

    @Test
    fun `should create start report at the beginning of uspek`() {
        uspek_test_1()
        assertThat(infos).isEqualTo(listOf(TestInfo("some test", CodeLocation("uspek_test_1.kt", 6), state = STARTED)))
    }

    @Test
    fun `should create start report at the beginning of nested test`() {
        uspek_test_2()
        assertThat(infos).contains(TestInfo("some nested test", CodeLocation("uspek_test_2.kt", 8), state = STARTED))
    }

    @Test
    fun `should create success report after finishing test with success`() {
        uspek_test_3()
        assertThat(infos).contains(TestInfo(location = CodeLocation("uspek_test_3.kt", 9), state = SUCCESS))
    }

    @Test
    fun `should create failure report after finishing test with error`() {
        uspek_test_4()
        val actual = infos.filter { it.state == FAILURE }
        val expected = TestInfo(location = CodeLocation("uspek_test_4.kt", 9),
                state = FAILURE, failureLocation = CodeLocation("uspek_test_4.kt", 10),
                failureCause = actual[0].failureCause!!)
        assertThat(actual).contains(expected)
    }

    @Test
    fun `should start all outer clauses in proper order`() {
        uspek_test_5()
        assertThat(infos)
                .containsSequence(listOf(
                        TestInfo("some test", CodeLocation("uspek_test_5.kt", 8), state = STARTED),
                        TestInfo("some nested test", CodeLocation("uspek_test_5.kt", 9), state = STARTED)))
    }

    @Test
    fun `should start all nested tests`() {
        uspek_test_6()
        assertThat(infos)
                .containsAll(listOf(
                        TestInfo("first test", CodeLocation("uspek_test_6.kt", 8), state = STARTED),
                        TestInfo("second test", CodeLocation("uspek_test_6.kt", 11), state = STARTED)))
    }

    @Test
    fun `should gather success from all nested tests`() {
        uspek_test_7()
        assertThat(infos)
                .containsAll(listOf(
                        TestInfo(location = CodeLocation("uspek_test_7.kt", lineNumber = 9), state = SUCCESS),
                        TestInfo(location = CodeLocation("uspek_test_7.kt", lineNumber = 13), state = SUCCESS)))
    }

    @Test
    fun `should gather failures from all nested tests`() {
        uspek_test_8()
        val actual = infos.filter { it.state == FAILURE }
        val expected = listOf(
                TestInfo(location = CodeLocation("uspek_test_8.kt", lineNumber = 9),
                        state = FAILURE, failureLocation = CodeLocation("uspek_test_8.kt", lineNumber = 10),
                        failureCause = actual[0].failureCause!!),
                TestInfo(location = CodeLocation("uspek_test_8.kt", lineNumber = 13),
                        state = FAILURE, failureLocation = CodeLocation("uspek_test_8.kt", lineNumber = 14),
                        failureCause = actual[1].failureCause!!))
        assertThat(actual).containsSequence(expected)
    }

    @Test
    fun `should gather all failures along with successes`() {
        uspek_test_9()
        val expected = listOf(
                TestInfo("some test", CodeLocation("uspek_test_9.kt", 8), state = STARTED),
                TestInfo("first test", CodeLocation("uspek_test_9.kt", 9), state = STARTED),
                TestInfo(location = CodeLocation("uspek_test_9.kt", lineNumber = 9),
                        state = FAILURE, failureLocation = CodeLocation("uspek_test_9.kt", lineNumber = 10),
                        failureCause = infos[2].failureCause!!),
                TestInfo("second test", CodeLocation("uspek_test_9.kt", 13), state = STARTED),
                TestInfo(location = CodeLocation("uspek_test_9.kt", lineNumber = 13), state = SUCCESS))
        assertThat(infos).isEqualTo(expected)
    }

    @Test
    fun `should execute tests which are nested multiple times`() {
        uspek_test_10()
        assertThat(infos)
                .containsSequence(listOf(TestInfo("some test", CodeLocation("uspek_test_10.kt", 8), state = STARTED),
                        TestInfo("first test", CodeLocation("uspek_test_10.kt", 9), state = STARTED),
                        TestInfo("second test", CodeLocation("uspek_test_10.kt", 12), state = STARTED),
                        TestInfo(location = CodeLocation("uspek_test_10.kt", lineNumber = 12), state = SUCCESS),
                        TestInfo("first test", CodeLocation("uspek_test_10.kt", 9), state = STARTED),
                        TestInfo(location = CodeLocation("uspek_test_10.kt", lineNumber = 9), state = SUCCESS)
                ))
    }
}

