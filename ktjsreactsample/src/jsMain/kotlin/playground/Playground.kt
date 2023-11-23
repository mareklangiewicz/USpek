package pl.mareklangiewicz.playground

import kotlinx.coroutines.*
import pl.mareklangiewicz.ktsample.*
import pl.mareklangiewicz.painting.*
import pl.mareklangiewicz.uspek.*
import react.*
import react.dom.html.ReactHTML.canvas
import react.dom.html.ReactHTML.div
import web.cssom.*

external interface PlaygroundProps : Props { var speed: Int }


val Playground = FC<PlaygroundProps> { props ->

    var tree by useState(GlobalUSpekContext.root)

    useEffectOnce {
        val job = MainScope().launch {
            suspek {
                clearCanvas()
                paintSomething()
                delay(400)
                // we don't need any setState here because tree is useState hook delegate property
                tree = GlobalUSpekContext.root.copy() // FIXME: is copy needed? check (and debug!) how react compares states
                testSomeMicroCalc()
                // this line will almost never be reached, because tests end with exceptions and are caught by suspek
                println("All tests already finished.")
            }
        }
        cleanup { job.cancel() }
    }
    div {
        className = ClassName("playground")
        div { className = ClassName("tests-side"); USpekTreeUi(tree) }
        div { className = ClassName("canvas-side"); CanvasSideUi() }
    }
}

fun ChildrenBuilder.CanvasSideUi() {
    div {
        className = ClassName("canvas")
        canvas {
            onClick = {
                val event = it.asDynamic()
                val color = pickColor(event.clientX - event.target.offsetLeft, event.clientY - event.target.offsetTop)
                // TODO: check what clientX/Y is
                println(color)
            }
        }
    }
}
