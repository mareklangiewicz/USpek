plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
}

defaultGroupAndVer(Deps.uspekx)
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
                api(project(":uspek"))
                api(Deps.kotlinxCoroutinesCore)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(Deps.junit5)
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
