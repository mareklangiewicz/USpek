package pl.mareklangiewicz.ktsample

import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import pl.mareklangiewicz.uspek.*

private const val maxLoopShort = 900
// private const val maxLoopShort = 9000
private const val maxLoopLong = 500_000
// private const val maxLoopLong = 5_000_000
// private const val maxLoopLong = 50_000_000

// The actual (the most sensible) concurrent test is [tests_concurrent_massively],
// that runs 4 long-lived coroutines at the same time (each with big computation loop).
// Others are just simplified variants mostly to compare logs orders and times.
class ConcurrentJUnit5Test {

  @Test fun tests_sequential_slowly() = runBlocking(Dispatchers.Default) { checkSequentialSlowly(maxLoopShort) }
  // measured: around 200-350ms for maxLoopShort == 900; around 7.5s for 9000

  @Test fun tests_concurrent_slowly() = runBlocking(Dispatchers.Default) { checkConcurrentSlowly(maxLoopShort) }
  // measured: around 150-350ms for maxLoopShort == 900; around 4.6s for 9000

  @Test fun tests_simple_massively() = checkSimpleMassively(maxLoopLong)
  // measured: around 250-350ms for maxLoopLong 500_000; 600-850ms for 5mln; 5.5s for 50mln

  @Test fun tests_sequential_massively() = runBlocking(Dispatchers.Default) { checkSequentialMassively(maxLoopLong) }
  // measured: around 105-150ms for maxLoopLong == 500_000; 600ms for 5mln; 5.3s for 50mln

  @Test fun tests_concurrent_massively() = runBlocking(Dispatchers.Default) { checkConcurrentMassively(maxLoopLong) }
  // measured: around 100-250ms for maxLoopLong == 500_000; 400-550ms for 5mln; 2.8-3.5s for 50mln

  // Mostly to demonstrate reporting with JUnit5 factory
  @TestFactory fun exampleFactory() = runTestUSpekJUnit5Factory {
    checkAddSlowly(666, 10, 20)
    checkAddSlowly(999, 50, 60)
  }

}
