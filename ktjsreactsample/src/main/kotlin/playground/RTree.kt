package playground

import pl.mareklangiewicz.uspek.USpekTree
import pl.mareklangiewicz.uspek.failed
import pl.mareklangiewicz.uspek.finished
import react.*
import react.dom.html.*
import react.dom.html.ReactHTML.div

fun ChildrenBuilder.rtree(tree: USpekTree) {
    div {
        className = "tree background " + tree.result
        div {
            className = "tree overlay " + tree.result
            div {
                className = "tree content"
                +tree.title
                for (branch in tree.branches.values) rtree(branch)
            }
        }
    }
}

private val USpekTree.title get() = if (failed) "$name .. FAILURE" else if (finished) "$name .. SUCCESS" else "$name .."

private val USpekTree.result get() = if (failed) "failure" else if (finished) "success" else ""
