package pl.mareklangiewicz.ktjvmsample

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.uspek.*
import java.util.Locale


/** micro debugging ;-) */
private fun ud(s: String) =
    println("ud [${Thread.currentThread().name.padEnd(40).substring(0, 40)}] [${getCurrentTimeString()}] $s")

private val ud get() = ud("")

private fun getCurrentTimeString() = System.currentTimeMillis().let { String.format(Locale.US, "%tT:%tL", it, it) }

private const val maxLoopShort = 9000
private const val maxLoopLong = 50_000_000

class ConcurrentTest {

    @Test fun tests_sequential_slowly() = runBlocking(Dispatchers.Default) {
        uspekLog = { }
        ud("start")
        val d1 = suspekAsync { checkAddSlowly(1, 1, 9000); ud("in1") }; ud("out1"); d1.await(); ud("after1")
        val d2 = suspekAsync { checkAddSlowly(2, 1, 9000); ud("in2") }; ud("out2"); d2.await(); ud("after2")
        ud("end")
    }

    @Test fun tests_concurrent_slowly() = runBlocking(Dispatchers.Default) {
        uspekLog = { }
        ud("start")
        val d1 = suspekAsync { checkAddSlowly(1, 1, maxLoopShort); ud("in1") }; ud("out1")
        val d2 = suspekAsync { checkAddSlowly(2, 1, maxLoopShort); ud("in2") }; ud("out2")
        d1.await(); ud("after1")
        d2.await(); ud("after2")
        ud("end")
    }

    @Test fun tests_simple_massively() { suspekBlocking {
        ud("start")
        checkAddFaster(100, 199, 1, maxLoopLong); ud("1")
        checkAddFaster(200, 299, 1, maxLoopLong); ud("2")
        checkAddFaster(300, 399, 1, maxLoopLong); ud("3")
        checkAddFaster(400, 499, 1, maxLoopLong); ud("4")
        ud("end")
    } }

    @Test fun tests_sequential_massively() = runBlocking(Dispatchers.Default) {
        ud("start")
        val d1 = suspekAsync { checkAddFaster(100, 199, 1, maxLoopLong); ud("in1") }; ud("out1"); d1.await(); ud("after1")
        val d2 = suspekAsync { checkAddFaster(200, 299, 1, maxLoopLong); ud("in2") }; ud("out2"); d2.await(); ud("after2")
        val d3 = suspekAsync { checkAddFaster(300, 399, 1, maxLoopLong); ud("in3") }; ud("out3"); d3.await(); ud("after3")
        val d4 = suspekAsync { checkAddFaster(400, 499, 1, maxLoopLong); ud("in4") }; ud("out4"); d4.await(); ud("after4")
        ud("end")
    }

    @Test fun tests_concurrent_massively() = runBlocking(Dispatchers.Default) {
        ud("start")
        val d1 = suspekAsync { checkAddFaster(100, 199, 1, maxLoopLong); ud("in1") }; ud("out1")
        val d2 = suspekAsync { checkAddFaster(200, 299, 1, maxLoopLong); ud("in2") }; ud("out2")
        val d3 = suspekAsync { checkAddFaster(300, 399, 1, maxLoopLong); ud("in3") }; ud("out3")
        val d4 = suspekAsync { checkAddFaster(400, 499, 1, maxLoopLong); ud("in4") }; ud("out4")
        d1.await(); ud("after1")
        d2.await(); ud("after2")
        d3.await(); ud("after3")
        d4.await(); ud("after4")
        ud("end")
    }

    @TestFactory fun exampleFactory() = suspekTestFactory {
        checkAddSlowly(666, 10, 20)
        checkAddSlowly(999, 50, 60)
    }

    suspend fun checkAddSlowly(addArg: Int, resultFrom: Int, resultTo: Int) {
        "create SUT for adding $addArg + $resultFrom..$resultTo" so {
            val sut = MicroCalc(666)

            "check add $addArg" so {
                for (i in resultFrom..resultTo) {
                    // generating tests in a loop is slow because it starts the loop
                    // again and again just to find and run first not-finished test
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
