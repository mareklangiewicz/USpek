package pl.mareklangiewicz.playground

import kotlinx.coroutines.*
import pl.mareklangiewicz.ktsample.*
import pl.mareklangiewicz.painting.*
import pl.mareklangiewicz.uspek.*
import react.*
import react.dom.html.ReactHTML.canvas
import react.dom.html.ReactHTML.div
import web.cssom.*

external interface PlaygroundProps : Props {
  var speed: Int
}

val Playground = FC<PlaygroundProps> { props ->

  val tree = useUSpekHook {
    clearCanvas()
    paintSomething()
    delay(props.speed.toLong())
    testSomeMicroCalc()
  }

  div {
    className = ClassName("playground")
    div { className = ClassName("tests-side"); USpekTreeUi(tree) }
    div { className = ClassName("canvas-side"); CanvasSideUi() }
  }
}

// let's try custom hook: https://github.com/JetBrains/kotlin-wrappers/blob/master/docs/guide/react.md#custom-hooks
fun useUSpekHook(code: suspend () -> Unit): USpekTree {

  var tree by useState(GlobalUSpekContext.root)

  // useEffectOnceWithCleanup/onCleanup were removed from kotlin-react (gone in 2026.9.2-19.3.0,
  // still present in 2025.11.12-19.2.0). The replacement is the suspend CoroutineScope overload of
  // useEffectOnce, which cancels its scope on cleanup -- so the hand-rolled MainScope().launch plus
  // onCleanup { job.cancel() } is now just the body.
  useEffectOnce {
    suspek {
      // we don't need any setState here because tree is useState hook delegate property
      tree = GlobalUSpekContext.root.copy() // FIXME: is copy needed? check (and debug!) how react compares states
      code()
    }
  }
  return tree
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
