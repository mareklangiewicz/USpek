package playground

import react.*
import react.dom.*
import kotlinx.coroutines.*
import painting.clearCanvas
import painting.paintSomething
import painting.pickColor
import pl.mareklangiewicz.uspek.*
import react.dom.html.ReactHTML.canvas
import react.dom.html.ReactHTML.div

external interface PlaygroundProps : Props { var speed: Int }

val Playground = FC<PlaygroundProps> { props ->

    var tree by useState(GlobalUSpekContext.root)

    useEffect {
        uspekLog = {
            println(it.status)
            // we don't need any setState here because tree is useState hook delegate property
            tree = GlobalUSpekContext.root.copy() // FIXME: is copy needed? check (and debug!) how react compares states
            paintSomething()
            if (it.finished) clearCanvas()
        }
        // FIXME: We should have scope cancelling when leaving playground
        MainScope().launch {
            delay(500) // temporary delay for experiments
            example()
            paintSomething()
        }
    }
    div {
        className = "playground"
        div { className = "tests-side"; rtree(tree) }
        div {
            className = "canvas-side"
            div {
                className = "canvas"
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
    }
}

fun RBuilder.playground(speed: Int = 400) = child(Playground) { attrs.speed = speed }
