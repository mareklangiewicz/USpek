package pl.mareklangiewicz.uspek

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ConcurrentTest {

    init {
        uspekLogger = { if (it.failed) println(it.status) }
    }

    @Test fun tests_sequential_slowly() = runBlocking(Dispatchers.Default) {
        val deferred1 = uspekAsync { checkAddSlowly(1, 1, 5_000) }
        deferred1.await()
        val deferred2 = uspekAsync { checkAddSlowly(2, 1, 5_000) }
        deferred2.await()
        Unit
    }

    @Test fun tests_concurrent_slowly() = runBlocking(Dispatchers.Default) {
        val deferred1 = uspekAsync { checkAddSlowly(1, 1, 5_000) }
        val deferred2 = uspekAsync { checkAddSlowly(2, 1, 5_000) }
        deferred1.await()
        deferred2.await()
        Unit
    }

    @Test fun tests_simple_massively() {
        uspekBlocking {
            checkAddFaster(100, 199, 1, 2_000_000_000); ".".p
            checkAddFaster(200, 299, 1, 2_000_000_000); ".".p
            checkAddFaster(300, 399, 1, 2_000_000_000); ".".p
            checkAddFaster(400, 499, 1, 2_000_000_000); ".".p
            checkAddFaster(500, 599, 1, 2_000_000_000); ".".p
            checkAddFaster(600, 699, 1, 2_000_000_000); ".".p
            checkAddFaster(700, 799, 1, 2_000_000_000); ".".p
            checkAddFaster(800, 899, 1, 2_000_000_000); ".".p
        }
    }

    @Test fun tests_sequential_massively() = runBlocking(Dispatchers.Default) {
        val deferred1 = uspekAsync { checkAddFaster(100, 199, 1, 2_000_000_000); ".".p }; deferred1.await()
        val deferred2 = uspekAsync { checkAddFaster(200, 299, 1, 2_000_000_000); ".".p }; deferred2.await()
        val deferred3 = uspekAsync { checkAddFaster(300, 399, 1, 2_000_000_000); ".".p }; deferred3.await()
        val deferred4 = uspekAsync { checkAddFaster(400, 499, 1, 2_000_000_000); ".".p }; deferred4.await()
        val deferred5 = uspekAsync { checkAddFaster(500, 599, 1, 2_000_000_000); ".".p }; deferred5.await()
        val deferred6 = uspekAsync { checkAddFaster(600, 699, 1, 2_000_000_000); ".".p }; deferred6.await()
        val deferred7 = uspekAsync { checkAddFaster(700, 799, 1, 2_000_000_000); ".".p }; deferred7.await()
        val deferred8 = uspekAsync { checkAddFaster(800, 899, 1, 2_000_000_000); ".".p }; deferred8.await()
        Unit
    }

    @Test fun tests_concurrent_massively() = runBlocking(Dispatchers.Default) {
        val deferred1 = uspekAsync { checkAddFaster(100, 199, 1, 2_000_000_000); ".".p }
        val deferred2 = uspekAsync { checkAddFaster(200, 299, 1, 2_000_000_000); ".".p }
        val deferred3 = uspekAsync { checkAddFaster(300, 399, 1, 2_000_000_000); ".".p }
        val deferred4 = uspekAsync { checkAddFaster(400, 499, 1, 2_000_000_000); ".".p }
        val deferred5 = uspekAsync { checkAddFaster(500, 599, 1, 2_000_000_000); ".".p }
        val deferred6 = uspekAsync { checkAddFaster(600, 699, 1, 2_000_000_000); ".".p }
        val deferred7 = uspekAsync { checkAddFaster(700, 799, 1, 2_000_000_000); ".".p }
        val deferred8 = uspekAsync { checkAddFaster(800, 899, 1, 2_000_000_000); ".".p }
        deferred1.await()
        deferred2.await()
        deferred3.await()
        deferred4.await()
        deferred5.await()
        deferred6.await()
        deferred7.await()
        deferred8.await()
        Unit
    }

    suspend fun checkAddSlowly(addArg: Int, resultFrom: Int, resultTo: Int) {
        "create SUT" o {
            val sut = MicroCalc(666)

            "check add $addArg" o {
                for (i in resultFrom..resultTo) {
                    // generating tests in a loop is slow because it starts the loop
                    // again and again just to find and run first not-finished test
                    "check add $addArg to $i" o {
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
        "create SUT and check add $addArgFrom .. $addArgTo" o {
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
