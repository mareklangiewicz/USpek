plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
}

defaultGroupAndVer(Deps.uspek)
description = USpekKonf.description

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    jsDefault(withNode = true)
    linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("test"))
            }
        }
    }
}

fun getExtraString(name: String) = rootProject.ext[name]?.toString()

publishing {

    // Configure all publications
    publications.withType<MavenPublication> {

//        // Stub javadoc.jar artifact
//        artifact(javadocJar.get())

        // Provide artifacts information requited by Maven Central
        pom {
            name.set("USpek")
            description.set("Micro tool for testing with syntax similar to Spek, but shorter.")
            url.set("https://github.com/langara/uspek")

            licenses {
                license {
                    name.set("Apache-2.0")
                    url.set("https://opensource.org/licenses/Apache-2.0")
                }
            }
            developers {
                developer {
                    id.set("langara")
                    name.set("Marek Langiewicz")
                    email.set("marek.langiewicz@gmail.com")
                }
            }
            scm {
                url.set("https://github.com/langara/uspek")
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        getExtraString("signing.keyId"),
        getExtraString("signing.key"),
        getExtraString("signing.password")
    )
    sign(publishing.publications)
}
