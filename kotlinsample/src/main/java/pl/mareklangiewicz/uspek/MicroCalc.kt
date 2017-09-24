package pl.mareklangiewicz.uspek


class MicroCalc(var result: Int) {
    fun add(x: Int) { result += x }
    fun multiplyBy(x: Int) { result *= x }
}