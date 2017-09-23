package pl.mareklangiewicz.uspek

sealed class Report {
    abstract val testLocation: CodeLocation

    data class Failure(override val testLocation: CodeLocation,
                       val assertionLocation: CodeLocation?,
                       val cause: Throwable?) : Report()

    data class Success(override val testLocation: CodeLocation) : Report()

    data class Start(val testName: String, override val testLocation: CodeLocation) : Report()
}