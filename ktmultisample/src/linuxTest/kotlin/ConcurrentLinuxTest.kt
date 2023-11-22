package pl.mareklangiewicz.ktsample

import kotlinx.coroutines.*
import pl.mareklangiewicz.kground.*
import pl.mareklangiewicz.uspek.*
import kotlin.test.*

private const val maxLoopShort = 900
// private const val maxLoopShort = 9000 // WARNING: this can take long time - kotlin/native is sloooow.
private const val maxLoopLong = 500_000
// private const val maxLoopLong = 5_000_000 // WARNING: this can take long time - kotlin/native is sloooow.
// private const val maxLoopLong = 50_000_000 // WARNING: this WILL take way too long time - kotlin/native is sloooow.

// The actual (the most sensible) concurrent test is [tests_concurrent_massively],
// that runs 4 long-lived coroutines at the same time (each with big computation loop).
// Others are just simplified variants mostly to compare logs orders and times.
class ConcurrentLinuxTest {

    @Test fun tests_sequential_slowly() = runBlocking(Dispatchers.Default) { checkSequentialSlowly(maxLoopShort) }
    // measured: around 1.5s for maxLoopShort == 900; 2m 30s for 9000

    @Test fun tests_concurrent_slowly() = runBlocking(Dispatchers.Default) { checkConcurrentSlowly(maxLoopShort) }
    // measured: around 800ms for maxLoopShort == 900; 1m 30s for 9000

    @Test fun tests_simple_massively() = checkSimpleMassively(maxLoopLong)
    // measured: around 30s for maxLoopLong 500_000; 5min for 5mln

    @Test fun tests_sequential_massively() = runBlocking(Dispatchers.Default) { checkSequentialMassively(maxLoopLong) }
    // measured: around 30s for maxLoopLong == 500_000; 5min for 5mln

    @Test fun tests_concurrent_massively() = runBlocking(Dispatchers.Default) { checkConcurrentMassively(maxLoopLong) }
    // measured: around 10-13s for maxLoopLong == 500_000; 2m 20s for 5mln

    // Mostly to compare reporting to [ConcurrentJUnit5Test.exampleFactory]
    @Test fun exampleRunTestUSpek() = runTestUSpek {
        "start".teePP
        checkAddSlowly(666, 10, 20)
        checkAddSlowly(999, 50, 60)
        "end".tee.unit
    }
}
