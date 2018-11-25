package pl.mareklangiewicz.uspek

import org.junit.Test
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import java.util.*

class USpekJUnitRunner(testClass: Class<*>) : Runner() {

    private val rootDescription = Description.createSuiteDescription(testClass.simpleName, UUID.randomUUID().toString())

    private val tree = TestTree()

    init {
        tree.reset()
        USpek.log = logToAll(logToTree(tree), ::logToConsole)
        val instance = testClass.newInstance()
        testClass.declaredMethods
                .filter { it.getAnnotation(Test::class.java) !== null }
                .forEach { it.invoke(instance) }
        tree.info.finished = true
        rootDescription.addChild(createDescriptions(tree, testClass.name))
    }

    override fun getDescription(): Description = rootDescription

    override fun run(notifier: RunNotifier) = runTree(tree, tree.info.name ?: "UNKNOWN NAME", notifier)

    private fun runTree(branchTree: TestTree, name: String, notifier: RunNotifier) {
        if (branchTree.subtrees.isEmpty()) {
            val description = branchTree.info.data as? Description
            notifier.fireTestStarted(description)
            logToConsole(branchTree.info)
            branchTree.info.run { when {
                failed -> {
                    notifier.fireTestFailure(Failure(description, branchTree.info.failureCause))
                    notifier.fireTestFinished(description)
                }
                finished -> {
                    notifier.fireTestFinished(description)
                }
                else -> throw IllegalStateException("Tree branch not finished")
            } }
        } else {
            branchTree.subtrees.forEach { runTree(it, name + "\n" + it.info.name, notifier) }
        }
    }

    private fun createDescriptions(testBranch: TestTree, testSuite: String): Description {
        val description = createDescription(testBranch, testSuite)
        testBranch.subtrees.forEach {
            val child = createDescriptions(it, testSuite + "." + testBranch.info.name)
            description.addChild(child)
        }
        return description
    }

    private fun createDescription(testBranch: TestTree, testSuite: String): Description {
        return if (testBranch.subtrees.isNotEmpty()) {
            Description.createSuiteDescription(testBranch.info.name, UUID.randomUUID().toString())
        } else {
            Description.createTestDescription(testSuite, testBranch.info.name)
        }.apply {
            testBranch.info.data = this
        }
    }
}
