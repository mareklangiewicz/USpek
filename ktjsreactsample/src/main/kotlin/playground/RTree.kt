package playground

import pl.mareklangiewicz.uspek.USpekTree
import pl.mareklangiewicz.uspek.failed
import pl.mareklangiewicz.uspek.finished
import react.*
import react.dom.*

fun RBuilder.rtree(tree: USpekTree) {
    div(classes = "tree background " + tree.result) {
        div(classes = "tree overlay " + tree.result) {
            div(classes = "tree content") {
                +tree.title
                for (branch in tree.branches.values) rtree(branch)
            }
        }
    }
}

private val USpekTree.title get() = if (failed) "$name .. FAILURE" else if (finished) "$name .. SUCCESS" else "$name .."

private val USpekTree.result get() = if (failed) "failure" else if (finished) "success" else ""
