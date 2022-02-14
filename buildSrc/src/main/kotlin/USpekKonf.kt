import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.*

object USpekKonf {
    const val name = "USpek"
    const val authorId = "langara"
    const val authorName = "Marek Langiewicz"
    const val authorEmail = "marek.langiewicz@gmail.com"
    const val description = "Micro tool for testing with syntax similar to Spek, but shorter."
    const val githubUrl = "https://github.com/langara/uspek"
    const val licenceName = "Apache-2.0"
    const val licenceUrl = "https://opensource.org/licenses/Apache-2.0"
}

fun MavenPublication.defaultUSpekPublication(javaDoc: TaskProvider<Jar>) {

    artifact(javaDoc.get())

    // Provide artifacts information requited by Maven Central
    pom {
        name.set(USpekKonf.name)
        description.set(USpekKonf.description)
        url.set(USpekKonf.githubUrl)

        licenses {
            license {
                name.set(USpekKonf.licenceName)
                url.set(USpekKonf.licenceUrl)
            }
        }
        developers {
            developer {
                id.set(USpekKonf.authorId)
                name.set(USpekKonf.authorName)
                email.set(USpekKonf.authorEmail)
            }
        }
        scm {
            url.set(USpekKonf.githubUrl)
        }
    }
}
