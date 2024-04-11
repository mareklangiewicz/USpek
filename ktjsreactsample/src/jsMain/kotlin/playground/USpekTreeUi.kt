package pl.mareklangiewicz.playground

import pl.mareklangiewicz.uspek.USpekTree
import pl.mareklangiewicz.uspek.failed
import pl.mareklangiewicz.uspek.finished
import react.ChildrenBuilder
import react.dom.html.ReactHTML.div
import web.cssom.ClassName

fun ChildrenBuilder.USpekTreeUi(tree: USpekTree) {
  div {
    className = ClassName("tree background " + tree.result)
    div {
      className = ClassName("tree overlay " + tree.result)
      div {
        className = ClassName("tree content")
        +tree.title
        for (branch in tree.branches.values) USpekTreeUi(branch)
      }
    }
  }
}

private val USpekTree.title get() = if (failed) "$name .. FAILURE" else if (finished) "$name .. SUCCESS" else "$name .."

private val USpekTree.result get() = if (failed) "failure" else if (finished) "success" else ""
