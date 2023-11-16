package pl.mareklangiewicz.ktsample

fun main(args: Array<String>) {
    println(EXAMPLE_TEXT)
    val calc = MicroCalc(0)
    println(calc.result)
    calc.add(10)
    println(calc.result)
}

