package mareklangiewicz.pl.uspek

import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import java.util.*

class USpekJUnitRunner(testClass: Class<Any>) : Runner() {

    private val rootDescription = Description.createSuiteDescription(testClass.simpleName, UUID.randomUUID().toString())

    private val treeCollectionLogger = TreeCollectionLogger()

    init {
        USpek.log = treeCollectionLogger
        testClass.newInstance()
        treeCollectionLogger.testTree?.state = TestState.SUCCESS
        rootDescription.addChild(createDescriptions(treeCollectionLogger.testTree!!, testClass.name))
    }

    override fun getDescription(): Description = rootDescription

    override fun run(notifier: RunNotifier) {
        println("USpek is running....")
        val testTree = treeCollectionLogger.testTree!!
        runTree(testTree, testTree.name, notifier)
    }

    private fun runTree(branchTree: TestTree, name: String, notifier: RunNotifier) {
        if (branchTree.subtests.isEmpty()) {
            val description = branchTree.description
            notifier.fireTestStarted(description)
            when (branchTree.state) {
                TestState.STARTED -> throw IllegalStateException("INTERNAL ERROR!!!")
                TestState.SUCCESS -> {
                    println("$name\nSUCCESS.${branchTree.location}\n")
                    notifier.fireTestFinished(description)
                }
                TestState.FAILURE -> {
                    println("$name\nFAILURE.${branchTree.location}")
                    println("BECAUSE.${branchTree.assertionLocation}")
                    println("${branchTree.failureCause}\n")
                    notifier.fireTestFinished(description)
                }
            }
        } else {
            branchTree.subtests.forEach { runTree(it, name + "\n" + it.name, notifier) }
        }
    }

    private fun createDescriptions(testBranch: TestTree, testSuite: String): Description {
        val description = createDescription(testBranch, testSuite)
        testBranch.subtests.forEach {
            val child = createDescriptions(it, testSuite + "." + testBranch.name)
            description.addChild(child)
        }
        return description
    }

    private fun createDescription(testBranch: TestTree, testSuite: String): Description {
        return if (testBranch.subtests.isNotEmpty()) {
            Description.createSuiteDescription(testBranch.name, UUID.randomUUID().toString())
        } else {
            Description.createTestDescription(testSuite, testBranch.name)
        }.apply {
            testBranch.description = this
        }
    }
}