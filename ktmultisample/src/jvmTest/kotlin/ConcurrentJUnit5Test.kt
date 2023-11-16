package pl.mareklangiewicz.ktsample

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.uspek.*
import java.nio.file.*
import java.util.Locale
import kotlin.time.*


private val String.utee get() =
    println("utee [${Thread.currentThread().name.padEnd(40).substring(0, 40)}] [${getCurrentTimeString()}] $this")

private fun getCurrentTimeString() = System.currentTimeMillis().let { String.format(Locale.US, "%tT:%tL", it, it) }

private const val maxLoopShort = 900
// private const val maxLoopShort = 9000
private const val maxLoopLong = 500_000
// private const val maxLoopLong = 5_000_000
// private const val maxLoopLong = 50_000_000

class ConcurrentJUnit5Test {

    @Test fun a_current_path() { // Just to check if we are running it in ktmultisample or via symlink in ktjunit5sample
        println(Paths.get("").toAbsolutePath())
    }

    @Test fun tests_sequential_slowly() = runBlocking(Dispatchers.Default) {
        uspekLog = { }
        "start".utee
        val time = measureTime {
            val d1 = asyncUSpek { checkAddSlowly(1, 1, maxLoopShort); "in1".utee }; "out1".utee; d1.await(); "after1".utee
            val d2 = asyncUSpek { checkAddSlowly(2, 1, maxLoopShort); "in2".utee }; "out2".utee; d2.await(); "after2".utee
        }
        "end (measured: $time)".utee // measured: around 200ms for maxLoopShort == 900; around 7.5s for 9000
    }

    @Test fun tests_concurrent_slowly() = runBlocking(Dispatchers.Default) {
        uspekLog = { }
        "start".utee
        val time = measureTime {
            val d1 = asyncUSpek { checkAddSlowly(1, 1, maxLoopShort); "in1".utee }; "out1".utee
            val d2 = asyncUSpek { checkAddSlowly(2, 1, maxLoopShort); "in2".utee }; "out2".utee
            d1.await(); "after1".utee
            d2.await(); "after2".utee
        }
        "end (measured: $time)".utee // measured: around 160ms for maxLoopShort == 900; around 4.6s for 9000
    }

    @Test fun tests_simple_massively() {
        "start".utee
        val time = measureTime {
            runBlockingUSpek { // measured: around 130ms for maxLoopLong 500_000; 600ms for 5mln; 5.5s for 50mln
            // runTestUSpek { // measured: around 180ms for maxLoopLong 500_000; 670ms for 5mln; 5.3s for 50mln
                checkAddFaster(100, 199, 1, maxLoopLong); "1".utee
                checkAddFaster(200, 299, 1, maxLoopLong); "2".utee
                checkAddFaster(300, 399, 1, maxLoopLong); "3".utee
                checkAddFaster(400, 499, 1, maxLoopLong); "4".utee
            }
        }
        "end (measured: $time)".utee
    }

    @Test fun tests_sequential_massively() = runBlocking(Dispatchers.Default) {
        "start".utee
        val time = measureTime {
            val d1 = asyncUSpek { checkAddFaster(100, 199, 1, maxLoopLong); "in1".utee }; "out1".utee; d1.await(); "after1".utee
            val d2 = asyncUSpek { checkAddFaster(200, 299, 1, maxLoopLong); "in2".utee }; "out2".utee; d2.await(); "after2".utee
            val d3 = asyncUSpek { checkAddFaster(300, 399, 1, maxLoopLong); "in3".utee }; "out3".utee; d3.await(); "after3".utee
            val d4 = asyncUSpek { checkAddFaster(400, 499, 1, maxLoopLong); "in4".utee }; "out4".utee; d4.await(); "after4".utee
        }
        "end (measured: $time)".utee // measured: around 105ms for maxLoopLong == 500_000; 600ms for 5mln; 5.3s for 50mln
    }

    @Test fun tests_concurrent_massively() = runBlocking(Dispatchers.Default) {
        "start".utee
        val time = measureTime {
            val d1 = asyncUSpek { checkAddFaster(100, 199, 1, maxLoopLong); "in1".utee }; "out1".utee
            val d2 = asyncUSpek { checkAddFaster(200, 299, 1, maxLoopLong); "in2".utee }; "out2".utee
            val d3 = asyncUSpek { checkAddFaster(300, 399, 1, maxLoopLong); "in3".utee }; "out3".utee
            val d4 = asyncUSpek { checkAddFaster(400, 499, 1, maxLoopLong); "in4".utee }; "out4".utee
            d1.await(); "after1".utee
            d2.await(); "after2".utee
            d3.await(); "after3".utee
            d4.await(); "after4".utee
        }
        "end (measured: $time)".utee // measured: around 120ms for maxLoopLong == 500_000; 390ms for 5mln; 3.3s for 50mln
    }

    @TestFactory fun exampleFactory() = runTestUSpekJUnit5Factory {
        checkAddSlowly(666, 10, 20)
        checkAddSlowly(999, 50, 60)
    }

    /** Slowly just because there is for loop with test inside - see longer comment below */
    suspend fun checkAddSlowly(addArg: Int, resultFrom: Int, resultTo: Int) {
        "create SUT for adding $addArg + $resultFrom..$resultTo" so {
            val sut = MicroCalc(666)

            "check add $addArg" so {
                for (i in resultFrom..resultTo) {
                    // generating tests in a loop is slow because it starts the loop
                    // again and again just to find and run first not-finished test
                    // it's technically correct, because each test have different name,
                    // but not really correct because unnecessary looping and comparing finished tests names each time.
                    // lesser issue is: generatig so many tests logs a lot by default.
                    // I'm leaving this code as is, as an interesting example of uspek behavior,
                    // even though you should never do things like this.
                    "check add $addArg to $i" so {
                        sut.result = i
                        sut.add(addArg)
                        sut.result eq i + addArg
//                        require(i < resultTo - 3) // this should fail three times
                    }
                }
            }
        }
    }

    suspend fun checkAddFaster(addArgFrom: Int, addArgTo: Int, resultFrom: Int, resultTo: Int) {
        "create SUT and check add $addArgFrom .. $addArgTo" so {
            val sut = MicroCalc(666)

            for (addArg in addArgFrom..addArgTo)
                for (i in resultFrom..resultTo) {
                    sut.result = i
                    sut.add(addArg)
                    sut.result eq i + addArg
                }
//            require(false) // enable to test failing
        }
    }
}
