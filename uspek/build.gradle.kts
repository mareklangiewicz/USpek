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

// Stub javadoc.jar artifact (sonatype requires javadoc artifact) FIXME: stop using stubs
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    publications.withType<MavenPublication> { defaultUSpekPublication(javadocJar) }
}

signing {
    useInMemoryPgpKeys(
        rootExt("signing.keyId"),
        rootExt("signing.key"),
        rootExt("signing.password")
    )
    sign(publishing.publications)
}
