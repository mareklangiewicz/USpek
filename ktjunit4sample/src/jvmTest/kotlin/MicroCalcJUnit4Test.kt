package pl.mareklangiewicz.ktsample

import org.junit.runner.*
import pl.mareklangiewicz.kground.*
import pl.mareklangiewicz.uspek.*

@RunWith(USpekJUnit4Runner::class)
class MicroCalcJUnit4Test {
  init {
    "INIT ${this::class.simpleName}".teePP
  }

  @USpekTestTree(18) fun microCalcTest() = testSomeMicroCalc()
  @USpekTestTree(2) fun loggingTest() = testSomeLogging()
}
