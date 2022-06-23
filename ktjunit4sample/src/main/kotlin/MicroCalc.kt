package pl.mareklangiewicz.ktjvmsample


class MicroCalc(var result: Int) {
    fun add(x: Int) { result += x }
    fun multiplyBy(x: Int) { result *= x }
    fun ensureResultIs(expectedResult: Int) =
        check(result == expectedResult) { "result is not: $expectedResult; it is actually: $result" }
}
