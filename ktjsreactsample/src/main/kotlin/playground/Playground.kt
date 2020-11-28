package playground

import react.*
import react.dom.*
import kotlinx.coroutines.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseMoveFunction
import kotlinx.html.onMouseMove
import painting.clearCanvas
import painting.paintSomething
import painting.pickColor
import pl.mareklangiewicz.uspek.USpekTree
import pl.mareklangiewicz.uspek.finished
import pl.mareklangiewicz.uspek.status
import pl.mareklangiewicz.uspek.uspekContext
import pl.mareklangiewicz.uspek.uspekLog

interface PlaygroundProps : RProps { var speed: Int }
interface PlaygroundState : RState { var tree: USpekTree }

class Playground(props: PlaygroundProps) : RComponent<PlaygroundProps, PlaygroundState>(props) {

    override fun PlaygroundState.init(props: PlaygroundProps) {
        tree = uspekContext.root
        uspekLog = {
            println(it.status)
            setState { tree = uspekContext.root }
            paintSomething()
            js("sleep(0.1)")
//            delay(40) // FIXME: make it work (it works on my homepage (with suspending uspek copied&pasted))
                // probably all uspek functions have to be either suspending or inline...
                // maybe it's possible to force react to update DOM, then sleep for 40ms (so it's animating and browser
                // have a chance to refresh each time) - probably not (without loosing stack frames / breaking uspek control flow)..

            if (it.finished) clearCanvas()
        }
    }

    override fun componentDidMount() {
        GlobalScope.launch {
            example()
            paintSomething()
        }
    }

    override fun RBuilder.render() {
        div(classes = "playground") {
            div(classes = "tests-side") { rtree(state.tree) }
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
}

fun RBuilder.playground(speed: Int = 400) = child(Playground::class) { attrs.speed = speed }
