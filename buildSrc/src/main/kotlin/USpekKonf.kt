import org.gradle.api.*
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.*
import org.gradle.kotlin.dsl.*
import java.io.*

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

fun Project.defaultUSpekPublishing(readmeFile: File = File(rootDir, "README.md")) {

    val readmeJavadocJar by tasks.registering(Jar::class) {
        from(readmeFile) // TODO_maybe: use dokka to create real docs? (but it's not even java..)
        archiveClassifier.set("javadoc")
    }

    extensions.configure(PublishingExtension::class) {
        publications.withType<MavenPublication> { defaultUSpekPublication(readmeJavadocJar) }
    }
}

private fun MavenPublication.defaultUSpekPublication(javaDocProvider: TaskProvider<Jar>) {

    artifact(javaDocProvider)
        // Adding javadoc artifact generates warnings like:
        // Execution optimizations have been disabled for task ':uspek:signJvmPublication'
        // It looks like a bug in kotlin multiplatform plugin:
        // https://youtrack.jetbrains.com/issue/KT-46466
        // FIXME_someday: Watch the issue.
        // If it's a bug in kotlin multiplatform then remove this comment when it's fixed.
        // Some related bug reports:
        // https://youtrack.jetbrains.com/issue/KT-47936
        // https://github.com/gradle/gradle/issues/17043

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
