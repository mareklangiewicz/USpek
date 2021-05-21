package playground

import react.*
import react.dom.*
import kotlinx.coroutines.*
import kotlinx.html.js.onClickFunction
import painting.clearCanvas
import painting.paintSomething
import painting.pickColor
import pl.mareklangiewicz.uspek.*

external interface PlaygroundProps : RProps { var speed: Int }

val Playground = functionalComponent<PlaygroundProps> { props ->

    var tree by useState(GlobalUSpekContext.root)

    useEffect(emptyList()) {
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
    div(classes = "playground") {
        div(classes = "tests-side") { rtree(tree) }
        div(classes = "canvas-side") {
            div(classes = "canvas") {
                canvas {
                    attrs {
                        onClickFunction = {
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
}

fun RBuilder.playground(speed: Int = 400) = child(Playground) { attrs.speed = speed }
