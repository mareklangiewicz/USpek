package pl.mareklangiewicz.uspek

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ConcurrentTest {

    init {
        uspekLogger = { if (it.failed) println(it.status) }
    }

    @Test fun tests_sequential() = runBlocking(Dispatchers.Default) {
        val deferred1 = uspekAsync { checkAdd(1, 1, 5_000) }
        deferred1.await()
        val deferred2 = uspekAsync { checkAdd(2, 1, 5_000) }
        deferred2.await()
        Unit
    }

    @Test fun tests_concurrent() = runBlocking(Dispatchers.Default) {
        val deferred1 = uspekAsync { checkAdd(1, 1, 5_000) }
        val deferred2 = uspekAsync { checkAdd(2, 1, 5_000) }
        deferred1.await()
        deferred2.await()
        Unit
    }

    suspend fun checkAdd(addArg: Int, resultFrom: Int, resultTo: Int) {
        "create SUT" o {
            val sut = MicroCalc(666)

            "check add $addArg" o {
                for (i in resultFrom..resultTo) {
                    "check add $addArg to $i" o {
                        sut.result = i
                        sut.add(addArg)
                        sut.result eq i + addArg
//                        require(i < 4997) // this should fail a few times
                    }
                }
            }
        }
    }
}
