@file:OptIn(ObsoleteWorkersApi::class)

package pl.mareklangiewicz.ktlinuxsample

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import pl.mareklangiewicz.uspek.*
import kotlin.native.concurrent.*
import kotlin.system.*
import kotlin.test.*
import kotlin.time.*
import kotlin.time.Duration.Companion.minutes


/** micro debugging ;-) */
private fun ud(s: String) =
    // See KT-60932 KT-54702
    println("ud [${Worker.current.name}] [${getCurrentTimeString()}] $s")

private val ud get() = ud("")

@Suppress("DEPRECATION")
private fun getCurrentTimeString() = getTimeMillis().toString()

private const val maxLoopShort = 900
// private const val maxLoopShort = 9000 // WARNING: this can take long time - kotlin/native is sloooow.
private const val maxLoopLong = 500_000
// private const val maxLoopLong = 5_000_000 // WARNING: this can take long time - kotlin/native is sloooow.
// private const val maxLoopLong = 50_000_000 // WARNING: this WILL take way too long time - kotlin/native is sloooow.

class ConcurrentLinuxTest {

    @Test fun tests_sequential_slowly() = runBlocking(Dispatchers.Default) {
        uspekLog = { }
        ud("start")
        val time = measureTime {
            val d1 = asyncUSpek { checkAddSlowly(1, 1, maxLoopShort); ud("in1") }; ud("out1"); d1.await(); ud("after1")
            val d2 = asyncUSpek { checkAddSlowly(2, 1, maxLoopShort); ud("in2") }; ud("out2"); d2.await(); ud("after2")
        }
        ud("end (measured: $time)") // measured: around 1.5s for maxLoopShort == 900; 2m 30s for 9000
    }

    @Test fun tests_concurrent_slowly() = runBlocking(Dispatchers.Default) {
        uspekLog = { }
        ud("start")
        val time = measureTime {
            val d1 = asyncUSpek { checkAddSlowly(1, 1, maxLoopShort); ud("in1") }; ud("out1")
            val d2 = asyncUSpek { checkAddSlowly(2, 1, maxLoopShort); ud("in2") }; ud("out2")
            d1.await(); ud("after1")
            d2.await(); ud("after2")
        }
        ud("end (measured: $time)") // measured: around 800ms for maxLoopShort == 900; 1m 30s for 9000
    }

    @Test fun tests_simple_massively() {
        ud("start")
        val time = measureTime {
            // runBlockingUSpek { // measured: around 29s for maxLoopLong 500_000; 5min for 5mln
            runTestUSpek(timeout = 10.minutes) { // measured: around 30s for maxLoopLong 500_000; 5min for 5mln
                checkAddFaster(100, 199, 1, maxLoopLong); ud("1")
                checkAddFaster(200, 299, 1, maxLoopLong); ud("2")
                checkAddFaster(300, 399, 1, maxLoopLong); ud("3")
                checkAddFaster(400, 499, 1, maxLoopLong); ud("4")
            }
        }
        ud("end (measured: $time)")
    }

    @Test fun tests_sequential_massively() = runBlocking(Dispatchers.Default) {
        ud("start")
        val time = measureTime {
            val d1 = asyncUSpek { checkAddFaster(100, 199, 1, maxLoopLong); ud("in1") }; ud("out1"); d1.await(); ud("after1")
            val d2 = asyncUSpek { checkAddFaster(200, 299, 1, maxLoopLong); ud("in2") }; ud("out2"); d2.await(); ud("after2")
            val d3 = asyncUSpek { checkAddFaster(300, 399, 1, maxLoopLong); ud("in3") }; ud("out3"); d3.await(); ud("after3")
            val d4 = asyncUSpek { checkAddFaster(400, 499, 1, maxLoopLong); ud("in4") }; ud("out4"); d4.await(); ud("after4")
        }
        ud("end (measured: $time)") // measured: around 30s for maxLoopLong == 500_000; 5min for 5mln
    }

    @Test fun tests_concurrent_massively() = runBlocking(Dispatchers.Default) {
        ud("start")
        val time = measureTime {
            val d1 = asyncUSpek { checkAddFaster(100, 199, 1, maxLoopLong); ud("in1") }; ud("out1")
            val d2 = asyncUSpek { checkAddFaster(200, 299, 1, maxLoopLong); ud("in2") }; ud("out2")
            val d3 = asyncUSpek { checkAddFaster(300, 399, 1, maxLoopLong); ud("in3") }; ud("out3")
            val d4 = asyncUSpek { checkAddFaster(400, 499, 1, maxLoopLong); ud("in4") }; ud("out4")
            d1.await(); ud("after1")
            d2.await(); ud("after2")
            d3.await(); ud("after3")
            d4.await(); ud("after4")
        }
        ud("end (measured: $time)") // measured: around 10s for maxLoopLong == 500_000; 2m 16s for 5mln
    }

    @Test fun exampleRunTestUSpek() = runTestUSpek {
        ud("start")
        checkAddSlowly(666, 10, 20)
        checkAddSlowly(999, 50, 60)
        ud("end")
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
                        require(i < resultTo - 3) // this should fail three times
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
