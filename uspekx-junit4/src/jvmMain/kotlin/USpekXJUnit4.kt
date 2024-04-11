package pl.mareklangiewicz.uspek

import org.junit.*
import org.junit.Assert.*
import org.junit.runners.*
import org.junit.runners.model.*
import java.lang.reflect.*

@Target(AnnotationTarget.FUNCTION)
annotation class USpekTestTree(val expectedCount: Int)

class USpekJUnit4Runner(klass: Class<*>) : BlockJUnit4ClassRunner(klass) {

  init {
    GlobalUSpekContext.root.run { branches.clear(); end = null; data = null }
  }

  override fun getChildren(): List<FrameworkMethod> = buildList {
    addAll(testClass.getAnnotatedMethods(Test::class.java))
    val uspekTestsMethods = testClass.getAnnotatedMethods(USpekTestTree::class.java)
    for (method in uspekTestsMethods) {
      for (i in 1..method.expectedUSpekTestCount!! + 1) // + 1 to additional check if no tests left to invoke
        add(USpekFrameworkMethod(method.method, i))
    }
  }

  override fun testName(method: FrameworkMethod): String =
    (method as? USpekFrameworkMethod)?.uspekTestName ?: method.name

  // Disabling validation because I get "No runnable methods" when no @Test methods in class.
  // TODO_someday: extend ParentRunner directly (instead of BlockJUnit4ClassRunner) and implement correct validations
  // https://www.mscharhag.com/java/understanding-junits-runner-architecture
  override fun collectInitializationErrors(errors: MutableList<Throwable>) = Unit
}

private val FrameworkMethod.expectedUSpekTestCount get() = getAnnotation(USpekTestTree::class.java)?.expectedCount

private class USpekFrameworkMethod(method: Method, val testNr: Int) : FrameworkMethod(method) {

  val expectedCount = expectedUSpekTestCount!!

  override fun equals(other: Any?) = this === other
  override fun hashCode() = super.hashCode() xor testNr

  val uspekTestName
    get() = super.getName() + when {
      testNr <= expectedCount -> " $testNr of $expectedCount"
      testNr == expectedCount + 1 -> " check if no tests left"
      else -> error("Incorrect number of framework methods generated.")
    }

  override fun invokeExplosively(target: Any?, vararg params: Any?) {
    check(params.isEmpty())
    invokeInUSpek(target)
  }

  @Synchronized
  private fun invokeInUSpek(target: Any?): Unit = with(GlobalUSpekContext) {
    try {
      branch = root
      super.invokeExplosively(target)
      assertTrue("Expected $expectedCount tests, but nr $testNr is not found", testNr == expectedCount + 1)
    } catch (e: USpekException) {
      branch.end = e
      uspekLog(branch)
      if (branch.failed) throw e.cause!!
      assertTrue("Expected $expectedCount tests, but looks like there is more.", testNr <= expectedCount)
    }
  }
}
