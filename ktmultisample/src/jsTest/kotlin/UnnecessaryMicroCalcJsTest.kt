package pl.mareklangiewicz.ktsample

import pl.mareklangiewicz.kground.*
import pl.mareklangiewicz.uspek.*
import kotlin.test.*

// This file here is temporary. Mostly to monitor how new IntelliJ versions deal with js tests.
// Looks like IntelliJ have problem adding a play button to js tests (but they are working on better support)
// Normally commonTest/MicroCalcCmnTest should be enough, and the play button there should let me choose js platform too.
// (but it only let me choose between jvm and linux)
// (also here in current version I don't have any play buttons at all)
// (I guess they first have to fix kotlin gradle plugin to support "--tests ..." on js)
// https://youtrack.jetbrains.com/issue/IDEA-338427/IDE-dies-not-recognize-jsIR-target-anymore
// TODO_maybe: experiment with Cypress or Playwright Intellij integration?
// https://blog.jetbrains.com/idea/2023/11/intellij-idea-2023-3-beta-2/#new-functionality-for-testing-javascript

class UnnecessaryMicroCalcJsTest {
  init {
    "INIT ${this::class.simpleName}".teePP
  }
  // to check in logs in which scenarios, this example test is executed.
  @Test fun unnecessaryJsExampleTest() = uspek { "example test" o { 2 + 3 eq 5; "2 + 3 = 5".tee } }
  @Test fun unnecessarilyRepeatedMicroCalcCmnTest() = uspek { testSomeMicroCalc() }
  @Test fun unnecessarilyRepeatedLoggingCmnTest() = uspek { testSomeLogging() }
}
