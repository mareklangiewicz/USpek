package pl.mareklangiewicz.ktsample

import pl.mareklangiewicz.kground.*

fun main() {
    "Main START".teePP
    val calc = MicroCalc(0)
    "Micro Calc result is ${calc.result}".tee
    calc.add(10)
    "Micro Calc result is ${calc.result}".tee
    "Main END".tee
}

