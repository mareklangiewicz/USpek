package pl.mareklangiewicz.playground

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.mareklangiewicz.painting.*
import pl.mareklangiewicz.uspek.GlobalUSpekContext
import pl.mareklangiewicz.uspek.finished
import pl.mareklangiewicz.uspek.status
import pl.mareklangiewicz.uspek.uspekLog
import react.FC
import react.Props
import react.dom.html.ReactHTML.canvas
import react.dom.html.ReactHTML.div
import react.useEffect
import react.useState
import web.cssom.ClassName

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
        className = ClassName("playground")
        div { className = ClassName("tests-side"); rtree(tree) }
        div {
            className = ClassName("canvas-side")
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
    }
}
