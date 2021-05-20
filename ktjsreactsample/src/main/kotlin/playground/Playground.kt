package playground

import react.*
import react.dom.*
import kotlinx.coroutines.*
import kotlinx.html.js.onClickFunction
import painting.clearCanvas
import painting.paintSomething
import painting.pickColor
import pl.mareklangiewicz.uspek.finished
import pl.mareklangiewicz.uspek.status
import pl.mareklangiewicz.uspek.uspekContext
import pl.mareklangiewicz.uspek.uspekLog

external interface PlaygroundProps : RProps { var speed: Int }

val Playground = functionalComponent<PlaygroundProps> { props ->

    var tree by useState(uspekContext.root)

    useEffect(emptyList()) {
        uspekLog = {
            println(it.status)
            // we don't need any setState here because tree is useState hook delegate property
            tree = uspekContext.root.copy() // FIXME: is copy needed? check (and debug!) how react compares states
            paintSomething()
    //            delay(40) // FIXME: make it work (it works on my homepage (with suspending uspek copied&pasted))
            // probably all uspek functions have to be either suspending or inline...
            // maybe it's possible to force react to update DOM, then sleep for 40ms (so it's animating and browser
            // have a chance to refresh each time) - probably not (without loosing stack frames / breaking uspek control flow)..

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
