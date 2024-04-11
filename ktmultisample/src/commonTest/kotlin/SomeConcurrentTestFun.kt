package pl.mareklangiewicz.ktsample

import kotlinx.coroutines.*
import pl.mareklangiewicz.kground.*
import pl.mareklangiewicz.uspek.*
import kotlin.time.*
import kotlin.time.Duration.Companion.minutes

suspend fun checkSequentialSlowly(max: Int = 900) = coroutineScope {
  uspekLog = { }
  "start tss $max".teePP
  val time = measureTime {
    val d1 = asyncUSpek { checkAddSlowly(1, 1, max); "in1".tee }; "out1".tee; d1.await(); "after1".tee
    val d2 = asyncUSpek { checkAddSlowly(2, 1, max); "in2".tee }; "out2".tee; d2.await(); "after2".tee
  }
  "end (measured: $time)".tee.unit
}

suspend fun checkConcurrentSlowly(max: Int = 900) = coroutineScope {
  uspekLog = { }
  "start tcs $max".teePP
  val time = measureTime {
    val d1 = asyncUSpek { checkAddSlowly(1, 1, max); "in1".tee }; "out1".tee
    val d2 = asyncUSpek { checkAddSlowly(2, 1, max); "in2".tee }; "out2".tee
    d1.await(); "after1".tee
    d2.await(); "after2".tee
  }
  "end (measured: $time)".tee.unit // measured: around 160ms for maxLoopShort == 900; around 4.6s for 9000
}


fun checkSimpleMassively(max: Int = 500_000) {
  "start tsim $max".teePP
  val time = measureTime {
    runTestUSpek(timeout = 10.minutes) { // I also experimented with runBlockingUSpek, and it takes very similar time.
      checkAddFaster(100, 199, 1, max); "1".tee
      checkAddFaster(200, 299, 1, max); "2".tee
      checkAddFaster(300, 399, 1, max); "3".tee
      checkAddFaster(400, 499, 1, max); "4".tee
    }
  }
  "end (measured: $time)".tee.unit
}


suspend fun checkSequentialMassively(max: Int = 500_000) = coroutineScope {
  "start tsem $max".teePP
  val time = measureTime {
    val d1 = asyncUSpek { checkAddFaster(100, 199, 1, max); "in1".tee }; "out1".tee; d1.await(); "after1".tee
    val d2 = asyncUSpek { checkAddFaster(200, 299, 1, max); "in2".tee }; "out2".tee; d2.await(); "after2".tee
    val d3 = asyncUSpek { checkAddFaster(300, 399, 1, max); "in3".tee }; "out3".tee; d3.await(); "after3".tee
    val d4 = asyncUSpek { checkAddFaster(400, 499, 1, max); "in4".tee }; "out4".tee; d4.await(); "after4".tee
  }
  "end (measured: $time)".tee.unit
}


suspend fun checkConcurrentMassively(max: Int = 500_000) = coroutineScope {
  "start tcm $max".teePP
  val time = measureTime {
    val d1 = asyncUSpek { checkAddFaster(100, 199, 1, max); "in1".tee }; "out1".tee
    val d2 = asyncUSpek { checkAddFaster(200, 299, 1, max); "in2".tee }; "out2".tee
    val d3 = asyncUSpek { checkAddFaster(300, 399, 1, max); "in3".tee }; "out3".tee
    val d4 = asyncUSpek { checkAddFaster(400, 499, 1, max); "in4".tee }; "out4".tee
    d1.await(); "after1".tee
    d2.await(); "after2".tee
    d3.await(); "after3".tee
    d4.await(); "after4".tee
  }
  "end (measured: $time)".tee.unit
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
