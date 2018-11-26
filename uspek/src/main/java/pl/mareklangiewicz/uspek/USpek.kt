package pl.mareklangiewicz.uspek

object USpek {

    private val status: MutableMap<TestTrace, TestInfo> = mutableMapOf()

    var log: ULog = ::logToConsole

    var skip: (name: String, trace: TestTrace) -> Boolean = { _, trace -> status[trace]?.finished == true }

    fun uspek(name: String, code: () -> Unit) {
        status.clear()
        start(name, currentStackTrace.testTrace)
        while (true) {
            try { code(); return }
            catch (e: TestEnd) { end(e.stackTrace.testTrace, e) }
        }
    }

    infix fun String.o(code: () -> Unit) = currentStackTrace.testTrace.let {
        skip(this, it) || throw try {
            start(this, it)
            code()
            TestEnd()
        }
        catch (e: TestEnd) { e }
        catch (e: Throwable) { TestEnd(e) }
    }

    infix fun String.ox(code: () -> Unit) = true

    private fun start(name: String, trace: TestTrace) {
        status[trace] = TestInfo(false, name, trace)
        log(status[trace]!!)
    }

    private fun end(trace: TestTrace, exception: TestEnd) {
        val info = TestInfo(
            finished = true,
            failureLocation = exception.causeLocation,
            failureCause = exception.cause
        )
        status[trace]!!.applyExistentFrom(info)
        log(status[trace]!!)
    }
}

object UUSpek {

    val context = Context()

    data class Context(
        val root: Tree = Tree("uspek"),
        var branch: Tree = root
    )

    data class Tree(
        val name: String,
        val branches: MutableMap<String, Tree> = mutableMapOf(),
        var end: End? = null
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

    // TODO: remove it; use nice recursive toString on Tree
    var log: (Tree) -> Unit = {
        val state = when {
            it.end === null -> "started"
            it.end?.cause == null -> "success."
            else -> "failed: ${it.end}"
        }
        println("${it.name} $state")
    }
}

