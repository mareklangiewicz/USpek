package pl.mareklangiewicz.ktjvmsample

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import pl.mareklangiewicz.uspek.*

class ConcurrentTest {

    @Test fun tests_sequential_slowly() = runBlocking(Dispatchers.Default) {
        uspekLog = { }
        "start".ud
        val d1 = suspekAsync { checkAddSlowly(1, 1, 9000); "in1".ud }; "out1".ud; d1.await(); "after1".ud
        val d2 = suspekAsync { checkAddSlowly(2, 1, 9000); "in2".ud }; "out2".ud; d2.await(); "after2".ud
        "end".ud
    }

    @Test fun tests_concurrent_slowly() = runBlocking(Dispatchers.Default) {
        uspekLog = { }
        "start".ud
        val d1 = suspekAsync { checkAddSlowly(1, 1, 9000); "in1".ud }; "out1".ud
        val d2 = suspekAsync { checkAddSlowly(2, 1, 9000); "in2".ud }; "out2".ud
        d1.await(); "after1".ud
        d2.await(); "after2".ud
        "end".ud
    }

    @Test fun tests_simple_massively() = suspekBlocking {
        "start".ud
        checkAddFaster(100, 199, 1, 2_000_000_000); "1".ud
        checkAddFaster(200, 299, 1, 2_000_000_000); "2".ud
        checkAddFaster(300, 399, 1, 2_000_000_000); "3".ud
        checkAddFaster(400, 499, 1, 2_000_000_000); "4".ud
        "end".ud
    }

    @Test fun tests_sequential_massively() = runBlocking(Dispatchers.Default) {
        "start".ud
        val d1 = suspekAsync { checkAddFaster(100, 199, 1, 2_000_000_000); "in1".ud }; "out1".ud; d1.await(); "after1".ud
        val d2 = suspekAsync { checkAddFaster(200, 299, 1, 2_000_000_000); "in2".ud }; "out2".ud; d2.await(); "after2".ud
        val d3 = suspekAsync { checkAddFaster(300, 399, 1, 2_000_000_000); "in3".ud }; "out3".ud; d3.await(); "after3".ud
        val d4 = suspekAsync { checkAddFaster(400, 499, 1, 2_000_000_000); "in4".ud }; "out4".ud; d4.await(); "after4".ud
        "end".ud
    }

    @Test fun tests_concurrent_massively() = runBlocking(Dispatchers.Default) {
        "start".ud
        val d1 = suspekAsync { checkAddFaster(100, 199, 1, 2_000_000_000); "in1".ud }; "out1".ud
        val d2 = suspekAsync { checkAddFaster(200, 299, 1, 2_000_000_000); "in2".ud }; "out2".ud
        val d3 = suspekAsync { checkAddFaster(300, 399, 1, 2_000_000_000); "in3".ud }; "out3".ud
        val d4 = suspekAsync { checkAddFaster(400, 499, 1, 2_000_000_000); "in4".ud }; "out4".ud
        d1.await(); "after1".ud
        d2.await(); "after2".ud
        d3.await(); "after3".ud
        d4.await(); "after4".ud
        "end".ud
    }

    suspend fun checkAddSlowly(addArg: Int, resultFrom: Int, resultTo: Int) {
        "create SUT" so {
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
