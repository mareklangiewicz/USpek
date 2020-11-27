plugins {
    kotlin("multiplatform")
    `maven-publish`
}

group = "com.github.langara.uspek"
version = "0.0.12"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js {
        nodejs()
        browser {
            testTask {
                enabled = System.getenv("JITPACK") != "true"
            }
        }
    }
    linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Deps.kotlinTestCommon)
                api(Deps.kotlinTestAnnotationsCommon)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(Deps.kotlinTestJUnit)
                implementation(Deps.junit5)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(Deps.kotlinTestJs)
            }
        }
        val linuxX64Main by getting
    }
}

//// Create sources Jar from main kotlin sources
//val sourcesJar by tasks.creating(Jar::class) {
//    group = JavaBasePlugin.DOCUMENTATION_GROUP
//    description = "Assembles sources JAR"
//    classifier = "sources"
//    from(sourceSets.getByName("main").allSource)
//}
//
//publishing {
//    publications {
//        create("default", MavenPublication::class.java) {
//            from(components.getByName("java"))
//            artifact(sourcesJar)
//        }
//    }
//    repositories {
//        maven {
//            url = uri("$buildDir/repository")
//        }
//    }
//}
