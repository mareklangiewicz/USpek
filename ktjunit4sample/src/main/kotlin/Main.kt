package pl.mareklangiewicz.ktsample

fun main(args: Array<String>) {
    val calc = MicroCalc(0)
    println(calc.result)
    calc.add(10)
    println(calc.result)
}

