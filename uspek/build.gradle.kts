plugins {
    kotlin("multiplatform")
    `maven-publish`
}

group = "com.github.langara.uspek"
version = "0.0.10"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
//    js {
//        browser()
//    }
//    linuxX64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-junit")
                implementation(Deps.junit5)
            }
        }
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
