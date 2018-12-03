package pl.mareklangiewicz.uspek

object USpek {

    val context = Context()

    data class Context(
        val root: Tree = Tree("uspek"),
        var branch: Tree = root
    )

    data class Tree(
        val name: String,
        val branches: MutableMap<String, Tree> = mutableMapOf(),
        var end: End? = null,
        var data: Any? = null
    )

    class End(cause: Throwable? = null) : RuntimeException(cause)

    fun uspek(code: () -> Unit) {
        while(true) try {
            context.branch = context.root
            code()
            return
        }
        catch (e: End) {
            context.branch.end = e
            log(context.branch)
        }
    }

    infix fun String.o(code: () -> Unit) {
        val branch = context.branch.branches[this] ?: Tree(this)
        branch.end === null || return
        context.branch.branches[this] = branch
        context.branch = branch
        log(branch)
        throw try { code(); End() }
        catch (e: End) { e }
        catch (e: Throwable) { End(e) }
    }

    infix fun String.ox(code: () -> Unit) = Unit

    var log: (Tree) -> Unit = this::logToConsole

    fun logToConsole(tree: Tree) = println(tree.run { when {
        failed -> "FAILURE.($location)\nBECAUSE.($causeLocation)\n"
        finished -> "SUCCESS.($location)\n"
        else -> name
    }})

    val Tree.finished get() = end !== null

    val Tree.failed get() = end?.cause !== null

    val Tree?.location get() = this?.end?.stackTrace?.testTrace?.get(0)?.location

    val Tree?.causeLocation get() = this?.end?.causeLocation
}

